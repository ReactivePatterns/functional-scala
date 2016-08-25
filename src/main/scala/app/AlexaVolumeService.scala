package app

import alexa.{AlexaVolume, AlexaVolumes, Volume}
import Constants._

import scala.util.{Failure, Success, Try}

object AlexaVolumeService extends VolumeService[String, AlexaVolume] {

  implicit def volumeToAlexaVolume(v: Volume[Int]) = v.asInstanceOf[AlexaVolume]

  private var state: AlexaVolume = AlexaVolumes.FIVE

  val up: Int => AlexaVolume = { l => AlexaVolumes(l + 1)}
  val down: Int => AlexaVolume = { l => AlexaVolumes(l - 1)}

  def lift[A](thunk: => A, sideEffect: A => Unit, message: String): Try[A] = {
    val t = Try[A] { thunk }
    t.foreach(sideEffect)
    if (t.isFailure) t.transform(v => Success(v), t => Failure(new Error(message))) else t
  }

  override def set(level: String): Try[AlexaVolume] = {
    lift[AlexaVolume]({ AlexaVolumes(level.toInt) }, state_=, LevelValidationMessage)
  }

  override def louder(): Try[AlexaVolume] = {
    lift[AlexaVolume]({ state.flatMap(up) }, state_=, OverUpperLimitMessage)
  }

  override def lower(): Try[AlexaVolume] = {
    lift[AlexaVolume]({ state.flatMap(down) }, state_=, UnderUpperLimitMessage)
  }
}
