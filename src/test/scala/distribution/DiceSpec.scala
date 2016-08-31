package distribution

import distribution.Distribution._
import org.scalatest.WordSpec

class DiceSpec extends WordSpec {

  val die = discreteUniform(1 to 6)

  "sample" in {
    println(die.sample(10))
  }

  "throw a 5" in {
    println(die.pr(_ == 5))
  }

  val dice = for {
    d1 <- die
    d2 <- die
  } yield d1 + d2

  "histogram of dice" in {
    dice.hist
  }

}

