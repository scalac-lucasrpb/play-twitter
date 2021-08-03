package repositories

import app.AppConfig
import com.datastax.oss.driver.api.core.cql.AsyncResultSet
import com.datastax.oss.driver.api.core.{CqlSession, DefaultConsistencyLevel}
import models.{FollowRequestData, GetTweetsRequestData, TweetData, UserData}
import org.slf4j.LoggerFactory
import play.api.Configuration

import java.nio.file.Paths
import javax.inject.{Inject, Singleton}
import scala.compat.java8.FutureConverters._
import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FeedRepository @Inject()(protected val config: Configuration)(implicit val ec: ExecutionContext)  {

  val logger = LoggerFactory.getLogger(this.getClass)

  val appConfig = config.get[AppConfig]("app.config")

  val ENV_CLIENT_ID = System.getenv("CLIENT_ID")
  val ENV_CLIENT_SECRET = System.getenv("CLIENT_SECRET")

  val CLIENT_ID = if(ENV_CLIENT_ID != null) ENV_CLIENT_ID else appConfig.clientId
  val CLIENT_SECRET = if(ENV_CLIENT_SECRET != null) ENV_CLIENT_SECRET else appConfig.clientSecret

  val session = CqlSession
    .builder()
    .withCloudSecureConnectBundle(Paths.get("secure-connect-scalac-twitter.zip"))
    .withAuthCredentials("SZMZicndkCbDDJBhsKbUQFcy", "es_,2R.MLHxFR2xpFosf4.ahL5r5E_s2cZfurPfAx6iC,h648uoroJz9+nWyqIzOk+7OZSo4iNy+gDttz3oNbKSES45W.RsHz4tEyxNTsOIG.lWmrGE,0ixK+yUyL+qr")
    .withKeyspace(appConfig.keyspace)
    .build()

  protected val INSERT_TWEET = session.prepare("insert into tweets(username, tweet_id, body, tags, tmp) values (?,?,?,?,?);")
  protected val GET_TWEETS = session.prepare("select * from tweets where username=? and tmp > ? order by tmp desc limit ?;")

  def tweet(data: TweetData): Future[Boolean] = {
    session.executeAsync(INSERT_TWEET.bind()
    .setString(0, data.username)
    .setString(1, data.id)
    .setString(2, data.body)
    .setSet(3, data.tags.asJava, classOf[String])
    .setLong(4, data.tmp)).toScala.map(_.wasApplied())
  }

  def getTweetsByUser(data: GetTweetsRequestData): Future[Seq[TweetData]] = {
    var tweets = Seq.empty[TweetData]
    session.executeAsync(GET_TWEETS.bind()
    .setString(0, data.username)
    .setLong(1, data.tmp)
    .setInt(2, data.limit)).toScala.flatMap { rs =>

      def next(rs: AsyncResultSet): Future[Seq[TweetData]] = {
        val it = rs.currentPage().iterator()

        while(it.hasNext){
          val one = it.next()
          tweets = tweets :+ TweetData(
            username = one.getString("username"),
            body = one.getString("body"),
            tags = one.getSet[String]("tags", classOf[String]).asScala.toSet,
            id = one.getString("tweet_id"),
            tmp = one.getLong("tmp")
          )
        }

        if(rs.hasMorePages){
          return rs.fetchNextPage().toScala.flatMap(next(_))
        }

        Future.successful(tweets)
      }

      next(rs)
    }
  }

}
