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

  override def set(level: String): Try[AlexaVolume] = {
    val vol: Try[AlexaVolume] = Try { AlexaVolumes(level.toInt) }
    vol match {
      case Success(a) => {
        state = a
        vol
      }
      case _ => Failure(new Exception("The volume must be a whole number between 0 and 10"))
    }

  }

  override def louder(): Try[AlexaVolume] = {
    val vol: Try[AlexaVolume] = Try { state.flatMap(up) }

    vol match {
      case Success(a) => {
        state = a
        vol
      }
      case _ => Failure(new Exception("Cannot go higher"))
    }
  }

  override def lower(): Try[AlexaVolume] = {
    val vol: Try[AlexaVolume] = Try { state.flatMap(down) }

    vol match {
      case Success(a) => {
        state = a
        vol
      }
      case _ => Failure(new Exception("Cannot go lower"))
    }
  }
}
