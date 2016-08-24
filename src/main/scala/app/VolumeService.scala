package app

import alexa.Volume

import scala.util.Try

trait VolumeService[L, V <: Volume[_]] {
  def set(level: L): Try[V]
  def louder(): Try[V]
  def lower(): Try[V]
}
