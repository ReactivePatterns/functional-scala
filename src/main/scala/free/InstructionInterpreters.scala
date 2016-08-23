package free

import cats._

import scala.concurrent.Future

object InstructionInterpreters {

  val idInterpreter = new (Instruction ~> Id) {
    override def apply[A](e: Instruction[A]): Id[A] = e match {
      case Produce(a) => a
      case Transform(f, in) => f(in)
    }
  }

  val futureInterpreter = new (Instruction ~> Future) {
    import scala.concurrent.ExecutionContext.Implicits.global

    override def apply[A](e: Instruction[A]): Future[A] = e match {
      case Produce(a) => Future {
        a
      }
      case Transform(f, in) => Future {
        f(in)
      }
    }
  }


  val logInterpreter = new (Instruction ~> Id) {
    override def apply[A](e: Instruction[A]): Id[A] = e match {
      case Produce(a) => {
        println(s"value: $a")
        a
      }
      case Transform(f, in) => {
        val out = f(in)
        println(s"input: $in, output: $out")
        out
      }
    }
  }

}
