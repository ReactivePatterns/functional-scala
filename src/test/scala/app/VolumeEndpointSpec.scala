package app

import akka.event.NoLogging
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest._

class VolumeEndpointSpec extends FlatSpec with Matchers with ScalatestRouteTest with VolumeUserService {
  override def testConfigSource = "akka.loglevel = WARNING"
  override def config = testConfig
  val logger = NoLogging

  "Endpoint" should "respond to a valid level submission" in {
    Post(s"/volume/set", SetVolume("7")) ~> routes ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[VolumeChanged].change.level shouldBe 7
    }
  }
}
