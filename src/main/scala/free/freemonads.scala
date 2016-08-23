package free

import cats.free.Free
import cats.{Id, ~>}
import cats.std.all._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

sealed trait External[A]
case class Tickets(count: Int) extends AnyVal
case class InvokeTicketingService(count: Int) extends External[Tickets]
case class UserTicketsRequest(ticketCount: Int)

object GetTicketsExample {

  def purchaseTickets(input: UserTicketsRequest): Free[External, Option[Tickets]] = {
    if (input.ticketCount > 0) {
      // creates a "Suspend" node
      Free.liftF(InvokeTicketingService(input.ticketCount)).map(Some(_))
    } else {
      Free.pure(None)
    }
  }

  def bonusTickets(purchased: Option[Tickets]): Free[External, Option[Tickets]] = {
    if (purchased.exists(_.count > 10)) {
      Free.liftF(InvokeTicketingService(1)).map(Some(_))
    } else {
      Free.pure(None)
    }
  }

  def formatResponse(purchased: Option[Tickets], bonus: Option[Tickets]): String =
    s"Purchased tickets: $purchased, bonus: $bonus"

  val input = UserTicketsRequest(11)

  val logic: Free[External, String] = for {
    purchased <- purchaseTickets(input)
    bonus <- bonusTickets(purchased)
  } yield formatResponse(purchased, bonus)

  val externalToServiceInvoker = new (External ~> Future) {
    override def apply[A](e: External[A]): Future[A] = e match {
      case InvokeTicketingService(c) => serviceInvoker.run(s"/tkts?count=$c")
    }
  }

  object serviceInvoker {
    def run(path: String) = {
      val c = path.split("=").toList.last.toInt
      Future {
        Tickets(c)
      }
    }
  }

  val testingInterpreter = new (External ~> Id) {
    override def apply[A](e: External[A]): Id[A] = e match {
      case InvokeTicketingService(c) => Tickets(c)
    }
  }

  def main(args: Array[String]): Unit = {
    val result = logic.foldMap(externalToServiceInvoker)
    result.foreach(println)

    val test = logic.foldMap(testingInterpreter)
    test.foreach(print)
  }
}

