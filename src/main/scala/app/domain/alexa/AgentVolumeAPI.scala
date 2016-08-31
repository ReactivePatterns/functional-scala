package app.domain.alexa

import akka.agent.Agent
import app.domain.Volume
import app.domain.alexa.Constants._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AgentVolumeAPI {

  implicit def volumeToAlexaVolume(v: Volume[Int]) = v.asInstanceOf[AlexaVolume]

  private val state = Agent[AlexaVolume](AlexaVolumes.FIVE)

  val up: Int => AlexaVolume = { l => AlexaVolumes(l + 1)}
  val down: Int => AlexaVolume = { l => AlexaVolumes(l - 1)}

  def lift[A](action: A => A, processor: (A => A) => Future[A], message: String): Future[A] = {
    val f: Future[A] = processor(action)
    f.transform(s => s, _ => new Error(message))
  }

  def set(level: String): Future[AlexaVolume] = {
    lift[AlexaVolume]({ _ => AlexaVolumes(level.toInt) }, state.alter, LevelValidationMessage)
  }

  def louder(): Future[AlexaVolume] = {
    lift[AlexaVolume]({ v: AlexaVolume => v.flatMap(up) }, state.alter, OverUpperLimitMessage)
  }

  def lower(): Future[AlexaVolume] = {
    lift[AlexaVolume]({ v: AlexaVolume => v.flatMap(down) }, state.alter, UnderLowerLimitMessage)
  }
}
