package app

import alexa.{AlexaVolume, AlexaVolumes, Volume}

import scala.util.{Try, Failure, Success}

trait VolumeService[L, V <: Volume[_]] {
  def set(level: L): Try[V]
  def louder(): Try[V]
  def lower(): Try[V]
}

object AlexaVolumeService extends VolumeService[String, AlexaVolume] {

  implicit def volumeToAlexaVolume(v: Volume[Int]) = v.asInstanceOf[AlexaVolume]

  private var state: AlexaVolume = AlexaVolumes.FIVE

  val up: Int => AlexaVolume = { l => AlexaVolumes(l + 1)}
  val down: Int => AlexaVolume = { l => AlexaVolumes(l - 1)}

  def process[A](thunk: => A, sideEffect: A => Unit, message: String): Try[A] = {
    val t = Try { thunk }
    t.foreach(sideEffect)
    t.transform(v => Success(v), t => Failure(new Error(message)))
  }

  override def set(level: String): Try[AlexaVolume] = {
    process[AlexaVolume]({ AlexaVolumes(level.toInt) }, state_=,
      "The volume must be a whole number between 0 and 10")
  }

  override def louder(): Try[AlexaVolume] = {
    process[AlexaVolume]({ state.flatMap(up) }, state_=,
      "Cannot go higher")
  }

  override def lower(): Try[AlexaVolume] = {
    process[AlexaVolume]({ state.flatMap(down) }, state_=,
      "Cannot go lower")
  }
}
