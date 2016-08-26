package app

import alexa.AlexaVolumes
import org.scalatest.{WordSpec, _}
import alexa.Constants._

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

    result.failed.get.getMessage shouldBe LevelValidationMessage
  }

  "cannot go higher than the maximum" in {
    val result = for {
      _ <- AlexaVolumeService.set("10")
      end <- AlexaVolumeService.louder()
    } yield end

    result.failed.get.getMessage shouldBe OverUpperLimitMessage
  }

  "cannot go lower than the minimum" in {
    val result = for {
      _ <- AlexaVolumeService.set("0")
      end <- AlexaVolumeService.lower()
    } yield end

    result.failed.get.getMessage shouldBe UnderUpperLimitMessage
  }


}
