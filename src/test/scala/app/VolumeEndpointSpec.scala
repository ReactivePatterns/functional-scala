package app

import akka.event.NoLogging
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import app.domain.{SetVolume, VolumeChanged, VolumeNotChanged, VolumeUp}
import app.domain.alexa.Constants._
import app.service.alexa._
import org.scalatest._

class VolumeEndpointSpec extends WordSpec with ShouldMatchers with ScalatestRouteTest with VolumeUserService {
  override def testConfigSource = "akka.loglevel = WARNING"
  override def config = testConfig
  val logger = NoLogging

  "Endpoint" should {
    "respond to a valid level submission" in {
      Post(s"/volume/set", SetVolume("7")) ~> routes ~> check {
        status shouldBe OK
        contentType shouldBe `application/json`
        responseAs[VolumeChanged].change.level shouldBe 7
      }
    }

    "respond to an invalid level submission" in {
      Post(s"/volume/set", SetVolume("7.5")) ~> routes ~> check {
        status shouldBe InternalServerError
        contentType shouldBe `application/json`
        responseAs[VolumeNotChanged].info shouldBe LevelValidationMessage
      }
    }

    "respond to a valid up submission" in {
      Post(s"/volume/set", SetVolume("3")) ~> routes
      Post(s"/volume/up", VolumeUp) ~> routes ~> check {
        status shouldBe OK
        contentType shouldBe `application/json`
        responseAs[VolumeChanged].change.level shouldBe 4
      }
    }
  }
}
