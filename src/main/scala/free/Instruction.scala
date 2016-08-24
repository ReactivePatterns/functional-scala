package free

import cats.free.Free

sealed trait Instruction[A]
case class Produce[A](a: A) extends Instruction[A]
case class Transform[In, Out](f: In => Out, in: In) extends Instruction[Out]

object Producer {
  def apply[A](f: => A): Free[Instruction, A] = Free.liftF(Produce(f)) //.pure(f)
}

case class Transformer[In, Out](f: In => Out) {
  def apply(in: In): Free[Instruction, Out] = Free.liftF(Transform(f, in))
}

object InstructionImplicits {
  implicit def Function0ToProducer[A](f: => A) = Producer(f)
  implicit def Function1ToTransformer[In, Out](f: In => Out) = Transformer(f)

}
