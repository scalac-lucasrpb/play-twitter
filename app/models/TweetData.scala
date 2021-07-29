package models

import play.api.libs.json.Json

import java.util.UUID

case class TweetData(username: String,
                     body: String,
                     tags: Set[String],
                     id: String = UUID.randomUUID().toString,
                     tmp: Long = System.currentTimeMillis())

object TweetData {
  implicit val tweetDataReads = Json.reads[TweetData]
  implicit val tweetDataWrites = Json.writes[TweetData]
}




