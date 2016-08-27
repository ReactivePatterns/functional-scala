package domain

trait Volume[A] {
  val level: A
  def flatMap[B](f: A => Volume[B]): Volume[B] = f(level)

  def map[B](f: A => B): Volume[B] = flatMap(a => Volume.unit[B](f(a)))

  override def toString: String = s"Volume at level $level"
}

object Volume {
  def unit[A](a: A): Volume[A] = new Volume[A] {
    override val level: A = a
  }
  def flatten[A](vv: Volume[Volume[A]]): Volume[A] = vv.flatMap(v => v)
  def compose[A, B, C](f: A => Volume[B], g: B => Volume[C]): A => Volume[C] =
    level => f(level).flatMap(g)
}
