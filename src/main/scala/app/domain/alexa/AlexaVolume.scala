package domain.alexa

import Constants._
import domain.Volume

sealed trait AlexaVolume extends Volume[Int]

object AlexaVolumes extends Enumeration {
  case class AlexaVolumeVal(level: Int) extends super.Val with AlexaVolume {
    require(level >=0 && level <= 10, LevelValidationMessage)
  }

  implicit def valueToVolume(v: Value) = v.asInstanceOf[AlexaVolume]

  val ZERO = new AlexaVolumeVal(0)
  val ONE = new AlexaVolumeVal(1)
  val TWO = new AlexaVolumeVal(2)
  val THREE = new AlexaVolumeVal(3)
  val FOUR = new AlexaVolumeVal(4)
  val FIVE = new AlexaVolumeVal(5)
  val SIX = new AlexaVolumeVal(6)
  val SEVEN = new AlexaVolumeVal(7)
  val EIGHT = new AlexaVolumeVal(8)
  val NINE = new AlexaVolumeVal(9)
  val TEN = new AlexaVolumeVal(10)

}

