package distribution

import java.util.concurrent.ThreadLocalRandom

import scala.annotation.tailrec
import scala.collection.parallel.immutable.ParSeq
import scala.math.BigDecimal

trait Distribution[A] {
  self =>
  protected def get: A

//  def map[B](f: A => B): Distribution[B] = new Distribution[B] {
//    override def get = f(self.get)
//  }

  def flatMap[B](f: A => Distribution[B]): Distribution[B] = new Distribution[B] {
    override def get = f(self.get).get
  }
  
  def map[B](f: A => B): Distribution[B] = flatMap(a => Distribution.unit[B](f(a)))


  override def toString = "<distribution>"

  ///////
  def filter(pred: A => Boolean): Distribution[A] = new Distribution[A] {
    @tailrec
    override def get = {
      val s = self.get
      if (pred(s)) s else this.get
    }
  }

  private val N = 10000

  def sample(n: Int = N): List[A] = List.fill(n)(self.get)

  def samplePar(n: Int = N): ParSeq[A] = (0 until N).par.map(i => self.get)

  def pr(pred: A => Boolean, given: A => Boolean = (a: A) => true, samples: Int = N): Double = {
    1.0 * this.filter(given).samplePar(samples).count(pred) / samples
  }

  ////
  def hist(implicit ord: Ordering[A] = null, d: A <:< Double = null) = {
    if (d == null) {
      plotHist(ord)
    } else {
      bucketedHist(20)(ord, d)
    }
  }

  def histData: Map[A, Double] = {
    this.sample(N).groupBy(x=>x).mapValues(_.length.toDouble / N)
  }

  private def plotHist(implicit ord: Ordering[A] = null) {
    val histogram = this.histData.toList
    val sorted = if (ord == null) histogram else histogram.sortBy(_._1)(ord)
    doPlot(sorted)
  }

  private def findBucketWidth(min: Double, max: Double, buckets: Int): (BigDecimal, BigDecimal, BigDecimal, Int) = {
    // Use BigDecimal to avoid annoying rounding errors.
    val widths = List(0.1, 0.2, 0.25, 0.5, 1.0, 2.0, 2.5, 5.0, 10.0).map(BigDecimal.apply)
    val span = max - min
    val p = (math.log(span) / math.log(10)).toInt - 1
    val scale = BigDecimal(10).pow(p)
    val scaledWidths = widths.map(_ * scale)
    val bestWidth = scaledWidths.minBy(w => (span / w - buckets).abs)
    val outerMin = (min / bestWidth).toInt * bestWidth
    val outerMax = ((max / bestWidth).toInt + 1) * bestWidth
    val actualBuckets = ((outerMax - outerMin) / bestWidth).toInt
    (outerMin, outerMax, bestWidth, actualBuckets)
  }

  def bucketedHist(buckets: Int)(implicit ord: Ordering[A], toDouble: A <:< Double) {
    val data = this.sample(N).toList.sorted
    val min = data.head
    val max = data.last
    val (outerMin, outerMax, width, nbuckets) = findBucketWidth(toDouble(min), toDouble(max), buckets)
    bucketedHistHelper(outerMin, outerMax, nbuckets, data, roundDown = false)(ord, toDouble)
  }

  def bucketedHist(min: Double, max: Double, nbuckets: Int, roundDown: Boolean = false)
                  (implicit ord: Ordering[A], toDouble: A <:< Double) {
    val data = this.sample(N).filter(a => {
      val x = toDouble(a)
      min <= x && x <= max
    }).sorted
    bucketedHistHelper(BigDecimal(min), BigDecimal(max), nbuckets, data, roundDown)(ord, toDouble)
  }

  private def bucketedHistHelper(min: BigDecimal, max: BigDecimal, nbuckets: Int, data: List[A], roundDown: Boolean)
                                (implicit ord: Ordering[A], toDouble: A <:< Double) {
    val rm = if (roundDown) BigDecimal.RoundingMode.DOWN else BigDecimal.RoundingMode.HALF_UP
    val width = (max - min) / nbuckets
    def toBucket(a: A): BigDecimal = ((toDouble(a) - min) / width).setScale(0, rm) * width + min
    val n = data.size
    val bucketToProb = data
      .groupBy(toBucket)
      .mapValues(_.size.toDouble / n)
    val bucketed = (min to max by width).map(a => a -> bucketToProb.getOrElse(a, 0.0))
    doPlot(bucketed)
  }

  private def doPlot[B](data: Iterable[(B, Double)]) = {
    val scale = 100
    val maxWidth = data.map(_._1.toString.length).max
    val fmt = "%"+maxWidth+"s %5.2f%% %s"
    data.foreach{ case (a, p) => {
      val hashes = (p * scale).toInt
      println(fmt.format(a.toString, p*100, "#" * hashes))
    }}
  }
}

object Distribution {

  def unit[A](a: A): Distribution[A] = new Distribution[A] {
    override val get: A = a
  }
  private val rand = ThreadLocalRandom.current()

  def discreteUniform[A](values: Iterable[A]): Distribution[A] = new Distribution[A] {
    private val vec = Vector() ++ values
    override def get = vec(rand.nextInt(vec.length))
  }
}

