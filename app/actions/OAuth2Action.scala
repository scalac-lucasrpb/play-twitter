package actions

import play.api.Logging
import play.api.mvc._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class OAuth2Action @Inject() (parser: BodyParsers.Default)(implicit ec: ExecutionContext)
  extends ActionBuilderImpl(parser)
    with Logging {
  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
    logger.info(s"${request.headers.get("Authorization")}")
    block(request)
  }
}
