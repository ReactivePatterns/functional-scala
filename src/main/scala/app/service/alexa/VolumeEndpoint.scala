package app.service.alexa

import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import domain.alexa.AlexaVolumeBehavior
import domain.alexa.AlexaVolumes.AlexaVolumeVal
import app.service.{AkkaHttpMicroservice, HttpService}
import spray.json.DefaultJsonProtocol

import scala.util.{Failure, Success}

sealed trait VolumeRequest
case class SetVolume(level: String) extends VolumeRequest
case object VolumeUp extends VolumeRequest
case object VolumeDown extends VolumeRequest


sealed trait VolumeResponse
case class VolumeChanged(change: AlexaVolumeVal) extends VolumeResponse
case class VolumeNotChanged(info: String) extends VolumeResponse

/**
  * Defines the JSON formatter for all our message types to support implicit marshalling/unmarshalling
  */
trait Protocols extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val setVolumeRequestFormat = jsonFormat1(SetVolume.apply)
  implicit val volumeUpRequestFormat = jsonFormat0(() => VolumeUp)
  implicit val volumeDownRequestFormat = jsonFormat0(() => VolumeDown)

  implicit val alexaVolumeResponseFormat = jsonFormat1(AlexaVolumeVal.apply)
  implicit val volumeChangedResponseFormat = jsonFormat1(VolumeChanged.apply)
  implicit val volumeNotChangedResponseFormat = jsonFormat1(VolumeNotChanged.apply)
}

trait VolumeUserService extends HttpService with Protocols {

  /**
    * Defines how HTTP requests and responses should be handled.
    */
  val routes = {
    logRequestResult(config.getString("services.name")) {
      pathPrefix("volume") {
        path("up") {
          (post & entity(as[VolumeUp.type])) { req =>
            AlexaVolumeBehavior.louder() match {
              case Success(value: AlexaVolumeVal) => complete(OK, VolumeChanged(value))
              case Failure(ex: IllegalArgumentException) => complete(BadRequest -> VolumeNotChanged(ex.getMessage))
              case Failure(ex) => complete(InternalServerError -> VolumeNotChanged(ex.getMessage))
            }
          }
        } ~
          path("down") {
            (post & entity(as[VolumeDown.type])) { req =>
              AlexaVolumeBehavior.lower() match {
                case Success(value: AlexaVolumeVal) => complete(OK, VolumeChanged(value))
                case Failure(ex: IllegalArgumentException) => complete(BadRequest -> VolumeNotChanged(ex.getMessage))
                case Failure(ex) => complete(InternalServerError -> VolumeNotChanged(ex.getMessage))
              }
            }
          } ~
          path("set") {
            (post & entity(as[SetVolume])) { req =>
              AlexaVolumeBehavior.set(req.level) match {
                case Success(value: AlexaVolumeVal) => complete(OK, VolumeChanged(value))
                case Failure(ex: IllegalArgumentException) => complete(BadRequest -> VolumeNotChanged(ex.getMessage))
                case Failure(ex) => complete(InternalServerError -> VolumeNotChanged(ex.getMessage))
              }
            }
          }
      }
    }
  }
}





