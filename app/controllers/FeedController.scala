package controllers

import actions.OAuth2Action
import models.{GetTweetsRequestData, TweetData}
import play.api.Logging
import play.api.libs.json.{JsNumber, JsObject, JsString, Json}
import play.api.mvc._
import repositories.FeedRepository

import java.util.UUID
import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class FeedController @Inject()(val controllerComponents: ControllerComponents,
                               val feedRepo: FeedRepository,
                               val outh2Action: OAuth2Action
                              )
                              (implicit val ec: ExecutionContext) extends BaseController with Logging {

  def tweet() = Action.async { implicit request: Request[AnyContent] =>

    val json = request.body.asJson

    if(json.isEmpty){
      Future.successful(BadRequest("Tweet data not found!"))
    } else {

      val data = json.get.as[JsObject] ++ Json.obj("tmp" -> JsNumber(System.currentTimeMillis()),
        "id" -> JsString(UUID.randomUUID().toString))

      val tweet = data.as[TweetData]

      logger.debug(s"\nTweet data: ${tweet}\n")

      feedRepo.tweet(tweet).map { ok =>
        Ok(Json.toJson(ok))
      }
    }
  }

  def tweetsByUser(username: String) = Action.async { implicit request: Request[AnyContent] =>

    val query = request.queryString
    val lastId = query.get("lastId")
    val limit = query.get("limit")

    val tr = GetTweetsRequestData(username, if(lastId.isDefined) Some(lastId.get.head) else None,
      if(limit.isDefined) limit.get.head.toInt else 100)

    logger.debug(s"\ntweets by user request: ${tr}\n")

    feedRepo.getTweetsByUser(tr).map { tweets =>
      Ok(Json.toJson(tweets))
    }
  }



}
