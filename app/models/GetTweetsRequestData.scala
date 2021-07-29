package models

import play.api.libs.json.Json

case class GetTweetsRequestData(username: String, tmp: Long = 0, lastId: Option[String] = None, limit: Int = 100)

object GetTweetsRequestData {
  implicit val tweetsDataReads = Json.reads[GetTweetsRequestData]
  implicit val tweetsDataWrites = Json.writes[GetTweetsRequestData]
}
