package app

import domain.alexa.{AlexaVolumeBehavior, AlexaVolumes}
import org.scalatest.{WordSpec, _}
import domain.alexa.Constants._

class VolumeServiceSpec extends WordSpec with ShouldMatchers {

  "sequence" in {
    val result = for {
      _ <- AlexaVolumeBehavior.set("7")
      _ <- AlexaVolumeBehavior.louder()
      _ <- AlexaVolumeBehavior.lower()
      end <- AlexaVolumeBehavior.lower()
    } yield end

    result.get shouldBe AlexaVolumes(6)
  }

  "not an int" in {
    val result = AlexaVolumeBehavior.set("7.5")

    result.failed.get.getMessage shouldBe LevelValidationMessage
  }

  "cannot go higher than the maximum" in {
    val result = for {
      _ <- AlexaVolumeBehavior.set("10")
      end <- AlexaVolumeBehavior.louder()
    } yield end

    result.failed.get.getMessage shouldBe OverUpperLimitMessage
  }

  "cannot go lower than the minimum" in {
    val result = for {
      _ <- AlexaVolumeBehavior.set("0")
      end <- AlexaVolumeBehavior.lower()
    } yield end

    result.failed.get.getMessage shouldBe UnderUpperLimitMessage
  }


}
