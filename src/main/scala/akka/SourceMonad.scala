package akka

import scala.concurrent.Future
import scala.concurrent.duration._
import akka.actor.ActorSystem
import akka.pattern.after
import akka.stream.scaladsl.{GraphDSL, Source, Zip}
import akka.stream.{ActorMaterializer, SourceShape}

object SourceMonad extends App {

  implicit val sys = ActorSystem()
  implicit val ec = sys.dispatcher
  implicit val materializer = ActorMaterializer()

  case class RichSource[A, M](val s: Source[A, M]) {
    def flatMap[B](f: A => Source[B, M]) = s.flatMapConcat[B, M](f)
  }
  implicit def sourceToMonad[A, M](s: Source[A, M]) = RichSource(s)
  implicit def valueToSource[A, M](a: A) = Source.single(a)
  implicit def valueToRichSource[A, M](a: A) = RichSource(Source.single(a))


  object RichSource {
    def apply2[A, B, C](fa: Source[A, _], fb: Source[B, _])(f: (A, B) => C): Source[C, NotUsed] = {
      Source.fromGraph(GraphDSL.create() { implicit builder =>
        import GraphDSL.Implicits._

        val zip = builder.add(Zip[A, B]())

        fa ~> zip.in0
        fb ~> zip.in1

        SourceShape(zip.out)
      }).map { case (a, b) => f(a, b) }
    }
  }

  val multiplied = Source(List(1,2,3)).map(_ * 2)
  val plusOne = multiplied.map(_ + 1)
  val result = plusOne.mapAsync(1)(x => after(50.milliseconds, sys.scheduler)(Future.successful(x))).map("test" + _)
  result.runForeach(println)

  val test = for {
    multiplied <- Source(List(1,2,3)).map(_ * 2)
    plusOne <- multiplied + 1
    result <- "test-" + plusOne
  } yield result

  test.runForeach(println)

  val pairMult = for {
    src1 <- Source(1 to 2)
    src2 <- Source(1 to 3)
    result <- "mult-" + src1 * src2
  } yield result

  pairMult.runForeach(println)

  val app = for {
    result <- RichSource.apply2(Source(1 to 100), Source(1 to 100))(_ * _ )
    plusOne <- result.mapAsync(5)(x => after(200.milliseconds, sys.scheduler)(Future.successful(x))).map(_ + 1)
  } yield plusOne

  app.runForeach(println)
}
