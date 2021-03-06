package app

import com.typesafe.config.Config
import play.api.ConfigLoader

case class AppConfig(keyspace: String)

object AppConfig {
  implicit val configLoader: ConfigLoader[AppConfig] = (rootConfig: Config, path: String) => {
    val config = rootConfig.getConfig(path)
    AppConfig(
      keyspace = config.getString("cql.keyspace")
    )
  }
}