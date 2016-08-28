package app.domain

import app.domain.alexa.AlexaVolumes.AlexaVolumeVal

sealed trait VolumeResponse
case class VolumeChanged(change: AlexaVolumeVal) extends VolumeResponse
case class VolumeNotChanged(info: String) extends VolumeResponse
