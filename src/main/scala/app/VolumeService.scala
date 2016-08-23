package app

import alexa.{AlexaVolume, AlexaVolumes, Volume}
import scala.{Either => _, Right => _, Left => _, _}
import _root_.util.{Either, Right, Left}

trait VolumeService[L, V <: Volume[_]] {
  def set(level: L): Either[String, V]
  def louder(): Either[String, V]
  def lower(): Either[String, V]
}

object AlexaVolumeService extends VolumeService[String, AlexaVolume] {

  implicit def volumeToAlexaVolume(v: Volume[Int]) = v.asInstanceOf[AlexaVolume]

  private var state: AlexaVolume = AlexaVolumes.FIVE

  private def Try[A](a: => A): Either[String, A] =
    try Right(a)
    catch { case e: Exception => Left(e.getMessage)}

  val up: Int => AlexaVolume = { l => AlexaVolumes(l + 1)}
  val down: Int => AlexaVolume = { l => AlexaVolumes(l - 1)}
  
  override def set(level: String): Either[String, AlexaVolume] = {
    val vol: Either[String, AlexaVolume] = Try { AlexaVolumes(level.toInt) }
    vol match {
      case Right(a) => {
        state = a
        vol
      }
      case _ => Left("The volume must be a whole number between 0 and 10")
    }

  }

  override def louder(): Either[String, AlexaVolume] = {
    val vol: Either[String, AlexaVolume] = Try { state.flatMap(up) }

    vol match {
      case Right(a) => {
        state = a
        vol
      }
      case _ => Left("Cannot go higher")
    }
  }

  override def lower(): Either[String, AlexaVolume] = {
    val vol: Either[String, AlexaVolume] = Try { state.flatMap(down) }

    vol match {
      case Right(a) => {
        state = a
        vol
      }
      case _ => Left("Cannot go lower")
    }
  }
}