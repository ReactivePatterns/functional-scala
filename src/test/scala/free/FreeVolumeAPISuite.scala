package free

import app.domain.alexa.AlexaVolumes
import app.domain.alexa.Constants._
import app.domain.free.{FreeVolumeAPI, Interpreters}
import app.domain.{Volume, VolumeRequest}
import cats.free.Free
import cats.std.future._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ShouldMatchers, WordSpec}

import scala.concurrent.ExecutionContext.Implicits.global

class FreeVolumeAPISuite extends WordSpec with ShouldMatchers with ScalaFutures {

  "logic" should {
    val logic: Free[VolumeRequest, Volume[_]] = for {
      _ <- FreeVolumeAPI.set("7")
      _ <- FreeVolumeAPI.louder()
      _ <- FreeVolumeAPI.lower()
      end <- FreeVolumeAPI.lower()
    } yield end

    "int interpreter" in {
      val result = logic.foldMap(Interpreters.mockInterpreter)
      result shouldBe AlexaVolumes(6)
    }

    "state interpreter" in {
      val futureResult = logic.foldMap(Interpreters.stateInterpreter)
      whenReady(futureResult) { result =>
        result shouldBe AlexaVolumes(6)
      }
    }

    "agent interpreter" in {
      val futureResult = logic.foldMap(Interpreters.agentInterpreter)
      whenReady(futureResult) { result =>
        result shouldBe AlexaVolumes(6)
      }
    }

    "cannot go higher than the maximum" should {

      "work with state" in {
        val over = for {
          _ <- FreeVolumeAPI.set("10")
          end <- FreeVolumeAPI.louder()
        } yield end

        val futureResult = over.foldMap(Interpreters.stateInterpreter)

        whenReady(futureResult.failed) { result =>
          result.getCause().getMessage shouldBe OverUpperLimitMessage
        }
      }

      "and mock" in {
        val over = for {
          _ <- FreeVolumeAPI.set("10")
          end <- FreeVolumeAPI.louder()
        } yield end

        val futureResult = over.foldMap(Interpreters.stateInterpreter)

        whenReady(futureResult.failed) { result =>
          result.getCause().getMessage shouldBe OverUpperLimitMessage
        }
      }
    }
  }
}

