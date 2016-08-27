package domain

import scala.util.Try

trait VolumeBehavior[L, V <: Volume[_]] {
  def set(level: L): Try[V]
  def louder(): Try[V]
  def lower(): Try[V]
}
