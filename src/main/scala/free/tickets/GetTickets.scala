package free.tickets

import cats.free.Free
import cats.std.all._
import cats.{Id, ~>}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


case class Tickets(count: Int)
case class UserTicketsRequest(ticketCount: Int)


sealed trait External[A]
case class InvokeTicketingService(count: Int) extends External[Tickets]

object GetTickets {

  def purchaseTickets(input: UserTicketsRequest): Free[External, Option[Tickets]] = {
    if (input.ticketCount > 0) {
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

  val asyncInterpreter = new (External ~> Future) {
    override def apply[Tickets](e: External[Tickets]): Future[Tickets] = e match {
      case InvokeTicketingService(c) => serviceInvoker.run(s"/tkts?count=$c")
    }
  }

  object serviceInvoker {
    def run(path: String) = {
      val c = path.split("=").toList.last.toInt
      Future[Tickets] {
        Tickets(c)
      }
    }
  }

  val syncInterpreter = new (External ~> Id) {
    override def apply[Tickets](e: External[Tickets]): Id[Tickets] = e match {
      case InvokeTicketingService(c) => Tickets(c)
    }
  }

  def main(args: Array[String]): Unit = {
    val result = logic.foldMap(asyncInterpreter)
    result.foreach(println)

    val test = logic.foldMap(syncInterpreter)
    test.foreach(print)
  }
}

