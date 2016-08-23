package app

import alexa.AlexaVolumes
import org.scalatest.{WordSpec, _}

import scala.{Either => _, Right => _, Left => _, _}
import _root_.util.{Right, Left}

class VolumeServiceSpec extends WordSpec with ShouldMatchers {

  "for comprehension" in {
    val result = for {
      _ <- AlexaVolumeService.set("7")
      _ <- AlexaVolumeService.louder()
      _ <- AlexaVolumeService.lower()
      end <- AlexaVolumeService.lower()
    } yield end

    result shouldBe Right(AlexaVolumes(6))
  }

  "not an int" in {
    val result = AlexaVolumeService.set("7.5")

    result shouldBe Left("The volume must be a whole number between 0 and 10")
  }

  "cannot go higher" in {
    val result = for {
      _ <- AlexaVolumeService.set("10")
      end <- AlexaVolumeService.louder()
    } yield end

    result shouldBe Left("Cannot go higher")
  }

  "cannot go lower" in {
    val result = for {
      _ <- AlexaVolumeService.set("0")
      end <- AlexaVolumeService.lower()
    } yield end

    result shouldBe Left("Cannot go lower")
  }


}
