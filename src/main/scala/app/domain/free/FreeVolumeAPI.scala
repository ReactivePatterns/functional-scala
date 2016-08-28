package app.domain.free

import app.domain.alexa.{AgentVolumeAPI, AlexaVolume, StateVolumeAPI, AlexaVolumes}
import app.domain._
import cats.{Id, ~>}
import cats.free.Free
import cats.std.future._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object FreeVolumeAPI {
  def set(level: String): Free[VolumeRequest, Volume[_]] = Free.liftF(SetVolume(level))
  def louder(): Free[VolumeRequest, Volume[_]] = Free.liftF(VolumeUp)
  def lower(): Free[VolumeRequest, Volume[_]] = Free.liftF(VolumeDown)
}

object Interpreters {

  val stateInterpreter = new (VolumeRequest ~> Future) {
    override def apply[AlexaVolume](e: VolumeRequest[AlexaVolume]): Future[AlexaVolume] = e match {
      case SetVolume(level) => {
        Future.fromTry(StateVolumeAPI.set(level))
      }
      case VolumeUp => {
        Future.fromTry(StateVolumeAPI.louder())
      }
      case VolumeDown => {
        Future.fromTry(StateVolumeAPI.lower())
      }
    }
  }

  val agentInterpreter = new (VolumeRequest ~> Future) {
    override def apply[AlexaVolume](e: VolumeRequest[AlexaVolume]): Future[AlexaVolume] = e match {
      case SetVolume(level) => {
        AgentVolumeAPI.set(level)
      }
      case VolumeUp => {
        AgentVolumeAPI.louder()
      }
      case VolumeDown => {
        AgentVolumeAPI.lower()
      }
    }
  }

  val mockInterpreter = new (VolumeRequest ~> Id) {
    private var state = 5
    override def apply[AlexaVolume](e: VolumeRequest[AlexaVolume]): Id[AlexaVolume] = e match {
      case SetVolume(level) => {
        state = level.toInt
        AlexaVolumes(state)
      }
      case VolumeUp => {
        state = state + 1
        AlexaVolumes(state)
      }
      case VolumeDown => {
        state = state - 1
        AlexaVolumes(state)
      }
    }
  }
}
