package filters

import javax.inject.{Inject, Singleton}
import akka.stream.Materializer
import play.api.Logging
import play.api.mvc._

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

@Singleton
class OAuth2Filter @Inject()(implicit val mat: Materializer, ec: ExecutionContext) extends Filter with Logging {
  def apply(nextFilter: RequestHeader => Future[Result])(requestHeader: RequestHeader): Future[Result] = {

    if(requestHeader.)    

    /*val startTime = System.currentTimeMillis

    nextFilter(requestHeader).map { result =>
      val endTime     = System.currentTimeMillis
      val requestTime = endTime - startTime

      logger.debug(
        s"${requestHeader.method} ${requestHeader.uri} took ${requestTime}ms and returned ${result.header.status}"
      )

      result.withHeaders("Request-Time" -> requestTime.toString)
    }*/
  }
}
