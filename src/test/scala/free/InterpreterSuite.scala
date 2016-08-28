package free

import app.domain.alexa.AlexaVolumes
import app.domain.free.{FreeVolumeAPI, Interpreters}
import app.domain.{Volume, VolumeRequest}
import cats.free.Free
import cats.std.future._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ShouldMatchers, WordSpec}

import scala.concurrent.ExecutionContext.Implicits.global

class InterpreterSuite extends WordSpec with ShouldMatchers with ScalaFutures {

  "logic" should {
    val logic: Free[VolumeRequest, Volume[_]] = for {
    _ <- FreeVolumeAPI.set("3")
    _ <- FreeVolumeAPI.louder()
    _ <- FreeVolumeAPI.louder()
    end <- FreeVolumeAPI.lower()
  } yield end

    "int interpreter" in {
      val result = logic.foldMap(Interpreters.mockInterpreter)
      result shouldBe AlexaVolumes(4)
    }

    "state interpreter"in {
      val futureResult = logic.foldMap(Interpreters.stateInterpreter)
      whenReady(futureResult) { result =>
        result shouldBe AlexaVolumes(4)
      }
    }

    "agent interpreter"in {
      val futureResult = logic.foldMap(Interpreters.agentInterpreter)
      whenReady(futureResult) { result =>
        result shouldBe AlexaVolumes(4)
      }
    }
  }
}

