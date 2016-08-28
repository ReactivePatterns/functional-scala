package agent

import akka.agent.Agent
import app.domain.alexa.AlexaVolumes
import app.domain.free.{FreeVolumeAPI, Interpreters}
import app.domain.{Volume, VolumeRequest}
import cats.free.Free
import cats.std.future._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ShouldMatchers, WordSpec}

import scala.concurrent.ExecutionContext.Implicits.global

class AgentSuite extends WordSpec with ShouldMatchers with ScalaFutures {

  "agents" should {
    val agent1 = Agent(3)
    val agent2 = Agent(5)

    "map" in {
      // uses map
      val result = for (value <- agent1) yield value + 1
      result.get() shouldBe 4
    }

    "for comprehension"in {
      // uses flatMap
      val agent = for {
        value1 <- agent1
        value2 <- agent2
      } yield value1 + value2


      whenReady(agent.future()) { result =>
        result shouldBe 8
      }
    }
  }
}

