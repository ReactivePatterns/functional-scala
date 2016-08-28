package distribution

import distribution.Distribution._
import org.scalatest.WordSpec

class MontyHallSpec extends WordSpec {

  val montyHall: Distribution[(Int, Int, Int)] = {
    val doors = (1 to 3).toSet
    for {
      prize <- discreteUniform(doors)   // The prize is placed randomly
      choice <- discreteUniform(doors)  // You choose randomly
      opened <- discreteUniform(doors - prize - choice)   // Monty opens one of the other doors
      switch <- discreteUniform(doors - choice - opened)  // You switch to the unopened door
    } yield (prize, choice, switch)
  }

  "monty hall" in {
    println("WINNING PROBABILITY IF STICKING TO CHOICE : " +
      montyHall.pr{ case (prize, choice, switch) => prize == choice }
    )
    println("WINNING PROBABILITY IF SWITCHING: " +
      montyHall.pr{ case (prize, choice, switch) => prize == switch }
    )
  }
 }

