package alexa

import app.domain.Volume
import org.scalatest.{WordSpec, _}


class VolumeSpec extends WordSpec with ShouldMatchers {

  val volume: Volume[Int] = Volume.unit(4)

  "unit" in {
    volume.level shouldBe 4
  }

  val louder: Int => Volume[Int] = {
    level: Int => Volume.unit(level + 1)
  }
  val step1 = volume.flatMap(louder)

  "flatMap" in {
    step1.level shouldBe 5
  }

  val increase: Int => Int = {
    level: Int => level + 1
  }
  val step2 = step1.map(increase)

  "map" in {
    step2.level shouldBe 6
  }

  val halfUp: Int => Volume[Double] = {
    level: Int => Volume.unit(level + 0.5)
  }
  val step3 = step2.flatMap(halfUp)

  "double" in {
    step3.level shouldBe 6.5
  }

  "for comprehension" in {
    val result = for {
      start <- volume
      up <- louder(start)
      end <- halfUp(up)
    } yield end

    result.level shouldBe 5.5
  }

}

