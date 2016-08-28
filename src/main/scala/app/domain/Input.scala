package app.domain

sealed trait VolumeRequest[A]
case class SetVolume(level: String) extends VolumeRequest[Volume[_]]
case object VolumeUp extends VolumeRequest[Volume[_]]
case object VolumeDown extends VolumeRequest[Volume[_]]
