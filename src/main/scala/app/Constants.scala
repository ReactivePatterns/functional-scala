package app

import com.typesafe.config.{Config, ConfigFactory}

object Constants {
  lazy val config: Config = ConfigFactory.load()
  final lazy val LevelValidationMessage: String = config.getString("messages.level.validation")
  final lazy val OverUpperLimitMessage: String = config.getString("messages.over.upper.limit")
  final lazy val UnderUpperLimitMessage: String = config.getString("messages.under.lower.limit")
}
