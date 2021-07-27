package actions

import play.api.Logging
import play.api.mvc._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OAuth2Action @Inject() (parser: BodyParsers.Default)(implicit ec: ExecutionContext)
  extends ActionBuilderImpl(parser)
    with Logging {
  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
    request.headers.get("Authorization") match {
      case None => Future.successful(Results.Unauthorized("Authorization token not found!"))
      case Some(auth) =>
        logger.debug(s"Authorization token found: ${auth}! Here we would check JWT user and secret!")
        block(request)
    }
  }
}
