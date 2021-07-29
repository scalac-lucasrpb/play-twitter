package controllers

import actions.OAuth2Action
import play.api.mvc._

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import models.{FollowRequestData, UserData}
import models.UserData._
import play.api.Logging
import play.api.libs.json.{JsNumber, JsObject, JsString, Json}
import repositories.UserRepository

import java.util.UUID

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class AccountController @Inject()(val controllerComponents: ControllerComponents,
                                  val userRepo: UserRepository,
                                  val outh2Action: OAuth2Action
                                 )
                                 (implicit val ec: ExecutionContext) extends BaseController with Logging {

  def index() = Action.async { implicit request: Request[AnyContent] =>

    val json = request.body.asJson

    if(json.isEmpty){
       Future.successful(BadRequest("User data not found!"))
    } else {

      val data = json.get.as[JsObject] ++ Json.obj("signup_tmp" -> JsNumber(System.currentTimeMillis()),
        "id" -> JsString(UUID.randomUUID().toString))

      val user = data.as[UserData]

      logger.debug(s"\nUser being inserted: ${user}\n")

      userRepo.insert(user).map { ok =>
        if(ok){
          Ok(Json.toJson(user))
        } else {
          BadRequest("user already exists!")
        }
      }
    }
  }

  def auth() = outh2Action.async { implicit request: Request[AnyContent] =>
    val json = request.body.asJson

    if(json.isEmpty){
      Future.successful(BadRequest("missing login info!"))
    } else {
      val data = json.get.as[JsObject]
      val username = data \ "username"
      val hash = data \ "hash"

      if(username.isEmpty || hash.isEmpty){
        Future.successful(BadRequest("missing login info!"))
      } else {
        userRepo.authByUsername(username.get.as[JsString].value, hash.get.as[JsString].value).map {
          case None => Unauthorized("wrong password and username combination!")
          case Some(user) => Ok(Json.toJson(user))
        }
      }
    }
  }

  def follow() = Action.async { implicit request: Request[AnyContent] =>

    val json = request.body.asJson

    if(json.isEmpty){
      Future.successful(BadRequest("Follow request data not found!"))
    } else {

      val data = json.get.as[JsObject] ++ Json.obj("tmp" -> JsNumber(System.currentTimeMillis()))

      val follow = data.as[FollowRequestData]

      logger.debug(s"\nFollowing request: ${follow}\n")

      userRepo.follow(follow).map { ok =>
        Ok(Json.toJson(ok))
      }
    }
  }

}
