package controllers

import actions.OAuth2Action
import akka.NotUsed
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.ws.TextMessage
import akka.stream.OverflowStrategy
import models.{GetTweetsRequestData, TweetData}
import org.reactivestreams.{Publisher, Subscriber}
import play.api.Logging
import play.api.libs.json.{JsNumber, JsObject, JsString, Json}
import play.api.mvc._
import repositories.FeedRepository

import java.util.UUID
import javax.inject._
import scala.collection.concurrent.TrieMap
import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class FeedController @Inject()(val system: ActorSystem,
                               val controllerComponents: ControllerComponents,
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

  import akka.stream.scaladsl._

  def tweetStream(username: String) = WebSocket.accept[String, String] { request =>
    // Log events to the console
    //val in = Sink.foreach[String](println)

    // Send a single 'Hello!' message and then leave the socket open
    //val out = Source.single("Hello!").concat(Source.maybe)
    val users = TrieMap.empty[String, ActorRef]

    var lastId = ""

    def fetch(): Unit = {
      feedRepo.getTweetsByUser(GetTweetsRequestData(username, Some(lastId), 10)).onComplete {
        case Success(tweets) =>

          tweets.foreach { t =>
            users.foreach { case (_, s) =>
               s ! TextMessage.Strict(t.body)
            }
          }

          if(!tweets.isEmpty){
            lastId = tweets.last.id
          }

          system.scheduler.scheduleOnce(100 millis)(fetch)
        case Failure(ex) => throw ex
      }
    }

    system.scheduler.scheduleOnce(100 millis)(fetch)

    val source = Source
      .actorRef(bufferSize = 1000, overflowStrategy = OverflowStrategy.dropBuffer)
      .mapMaterializedValue { user =>
        // the wsHandle is the way to talk back to the user, our wsUser actor needs to know about this to send
        // messages to the WebSocket user

        users.put(user.path.name, user)

        // don't expose the wsHandle anymore
        NotUsed
      }
      .keepAlive(maxIdle = 60.seconds, () => TextMessage.Strict("Keep-alive message sent to WebSocket recipient"))

    Flow.fromSinkAndSource(Sink.ignore, source).map(_.text)
  }



}
