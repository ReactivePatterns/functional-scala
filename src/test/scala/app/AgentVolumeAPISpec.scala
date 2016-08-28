package app

import app.domain.alexa.Constants._
import app.domain.alexa.{AgentVolumeAPI, AlexaVolumes}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ShouldMatchers, WordSpec}


class AgentVolumeAPISpec extends WordSpec with ShouldMatchers with ScalaFutures {

  import scala.concurrent.ExecutionContext.Implicits.global

  "sequence" in {
    val futureResult = for {
      _ <- AgentVolumeAPI.set("7")
      _ <- AgentVolumeAPI.louder()
      _ <- AgentVolumeAPI.lower()
      end <- AgentVolumeAPI.lower()
    } yield end

    whenReady(futureResult) { result =>
      result shouldBe AlexaVolumes(6)
    }
  }

  "not an int" in {
    val futureResult = AgentVolumeAPI.set("7.5")

    whenReady(futureResult.failed) { result =>
      result.getCause.getMessage shouldBe LevelValidationMessage
    }
  }

  "cannot go higher than the maximum" in {
    val futureResult = for {
      _ <- AgentVolumeAPI.set("10")
      end <- AgentVolumeAPI.louder()
    } yield end

    whenReady(futureResult.failed) { result =>
      result.getCause.getMessage shouldBe OverUpperLimitMessage
    }
  }

  "cannot go lower than the minimum" in {
    val futureResult = for {
      _ <- AgentVolumeAPI.set("0")
      end <- AgentVolumeAPI.lower()
    } yield end

    whenReady(futureResult.failed) { result =>
      result.getCause.getMessage shouldBe UnderUpperLimitMessage
    }
  }


}
