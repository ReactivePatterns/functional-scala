package app

import alexa.AlexaVolumes
import org.scalatest.{WordSpec, _}

import scala.{Either => _, Left => _, Right => _, _}
import _root_.util.{Left, Right}

import scala.util.Success

class VolumeServiceSpec extends WordSpec with ShouldMatchers {

  "sequence" in {
    val result = for {
      _ <- AlexaVolumeService.set("7")
      _ <- AlexaVolumeService.louder()
      _ <- AlexaVolumeService.lower()
      end <- AlexaVolumeService.lower()
    } yield end

    result.get shouldBe AlexaVolumes(6)
  }

  "not an int" in {
    val result = AlexaVolumeService.set("7.5")

    result.failed.get.getMessage shouldBe "The volume must be a whole number between 0 and 10"
  }

  "cannot go higher" in {
    val result = for {
      _ <- AlexaVolumeService.set("10")
      end <- AlexaVolumeService.louder()
    } yield end

    result.failed.get.getMessage shouldBe "Cannot go higher"
  }

  "cannot go lower" in {
    val result = for {
      _ <- AlexaVolumeService.set("0")
      end <- AlexaVolumeService.lower()
    } yield end

    result.failed.get.getMessage shouldBe "Cannot go lower"
  }


}
