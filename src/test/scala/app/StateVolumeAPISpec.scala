package app

import app.domain.alexa.{StateVolumeAPI, AlexaVolumes}
import org.scalatest.{WordSpec, _}
import app.domain.alexa.Constants._

class StateVolumeAPISpec extends WordSpec with ShouldMatchers {

  "sequence" in {
    val result = for {
      _ <- StateVolumeAPI.set("7")
      _ <- StateVolumeAPI.louder()
      _ <- StateVolumeAPI.lower()
      end <- StateVolumeAPI.lower()
    } yield end

    result.get shouldBe AlexaVolumes(6)
  }

  "not an int" in {
    val result = StateVolumeAPI.set("7.5")

    result.failed.get.getMessage shouldBe LevelValidationMessage
  }

  "cannot go higher than the maximum" in {
    val result = for {
      _ <- StateVolumeAPI.set("10")
      end <- StateVolumeAPI.louder()
    } yield end

    result.failed.get.getMessage shouldBe OverUpperLimitMessage
  }

  "cannot go lower than the minimum" in {
    val result = for {
      _ <- StateVolumeAPI.set("0")
      end <- StateVolumeAPI.lower()
    } yield end

    result.failed.get.getMessage shouldBe UnderLowerLimitMessage
  }


}
