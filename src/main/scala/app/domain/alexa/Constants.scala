package app.domain.alexa

import com.typesafe.config.{Config, ConfigFactory}

object Constants {
  lazy val config: Config = ConfigFactory.load()
  final lazy val LevelValidationMessage: String = config.getString("messages.level.validation")
  final lazy val OverUpperLimitMessage: String = config.getString("messages.over.upper.limit")
  final lazy val UnderLowerLimitMessage: String = config.getString("messages.under.lower.limit")
}
