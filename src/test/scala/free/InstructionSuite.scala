package free

import cats.free.Free
import cats.std.future._
import free.functions.{Instruction, Transformer}

import scala.concurrent.ExecutionContext.Implicits.global
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ShouldMatchers, WordSpec}

class InstructionSuite extends WordSpec with ShouldMatchers with ScalaFutures {

  val f_str = { i: Int => i.toString }
  val f_bang = { s: String => s + "!" }
  val f_hash = { s: String => s + "#" }
  val f_concat = { s: (String, String) => s._1 + s._2 }

  "function examples" should {

    val logic: String = {
      val i = 5
      val s = f_str(i)
      val b = f_bang(s)
      val h = f_hash(s)
      f_concat(b, h)
    }

    "composition" in {
      logic shouldBe "5!5#"
    }
  }

  "lifting examples" should {

    val start = 5
    val str: Transformer[Int, String] = f_str
    val bang: Transformer[String, String] = f_bang
    val hash: Transformer[String, String] = f_hash
    val concat: Transformer[(String, String), String] = f_concat

    type Logic[_] = Free[Instruction, _]

    val logic: Logic[String] = for {
      i <- start
      s <- str(i)
      b <- bang(s)
      h <- hash(s)
      c <- concat(b, h)
    } yield c

    "identity" in {

      val result = logic.foldMap(idInterpreter)

      result shouldBe "5!5#"
    }

    "future" in {

      val futureResult = logic.foldMap(futureInterpreter)

      whenReady(futureResult) { result =>
        result shouldBe "5!5#"
      }
    }

    "log" in {
      logic.foldMap(logInterpreter)
    }
  }
}

