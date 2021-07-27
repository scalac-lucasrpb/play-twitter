package models

import play.api.libs.json.{JsObject, Json}

import java.util.UUID

case class UserData(username: String, password: String, email: String,
                    id: String = UUID.randomUUID().toString,
                    signup_tmp: Long = System.currentTimeMillis())

object UserData {
  implicit val userDataReads = Json.reads[UserData]
  implicit val userDataWrites = Json.writes[UserData]
}
