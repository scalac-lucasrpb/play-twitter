package app

import com.typesafe.config.Config
import play.api.ConfigLoader

case class AppConfig(keyspace: String, clientId: String, clientSecret: String)

object AppConfig {
  implicit val configLoader: ConfigLoader[AppConfig] = (rootConfig: Config, path: String) => {
    val config = rootConfig.getConfig(path)
    AppConfig(
      keyspace = config.getString("cql.keyspace"),
      clientId = config.getString("cql.client_id"),
      clientSecret = config.getString("cql.client_secret")
    )
  }
}