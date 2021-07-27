package app

import com.typesafe.config.Config
import play.api.ConfigLoader

case class AppConfig(title: String)

object AppConfig {
  implicit val configLoader: ConfigLoader[AppConfig] = (rootConfig: Config, path: String) => {
    val config = rootConfig.getConfig(path)
    AppConfig(
      title = config.getString("cql.keyspace")
    )
  }
}