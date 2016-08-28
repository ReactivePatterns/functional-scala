package app.domain

import scala.util.Try

trait VolumeAPI[L, V <: Volume[_]] {
  def set(level: L): Try[V]
  def louder(): Try[V]
  def lower(): Try[V]
}
