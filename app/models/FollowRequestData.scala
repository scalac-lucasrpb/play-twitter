package models

import play.api.libs.json.Json

case class FollowRequestData(follower: String, followee: String, tmp: Long = System.currentTimeMillis())

object FollowRequestData {
  implicit val frDataReads = Json.reads[FollowRequestData]
  implicit val frDataWrites = Json.writes[FollowRequestData]
}


