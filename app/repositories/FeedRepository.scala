package repositories

import app.AppConfig
import com.datastax.oss.driver.api.core.cql.AsyncResultSet
import com.datastax.oss.driver.api.core.{CqlSession, DefaultConsistencyLevel}
import models.{FollowRequestData, GetTweetsRequestData, TweetData, UserData}
import play.api.Configuration

import javax.inject.{Inject, Singleton}
import scala.compat.java8.FutureConverters._
import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FeedRepository @Inject()(protected val config: Configuration)(implicit val ec: ExecutionContext)  {

  val appConfig = config.get[AppConfig]("app.config")

  val session = CqlSession
    .builder()
    //.addContactPoint(new InetSocketAddress(Config.CASSANDRA_HOST, Config.CASSANDRA_PORT))
    //.withConfigLoader(loader)
    //.withLocalDatacenter(Config.DC)
    .withKeyspace(appConfig.keyspace)
    .build()

  protected val INSERT_TWEET = session.prepare("insert into tweets(username, tweet_id, body, tags, tmp) values (?,?,?,?,?);")
  protected val GET_TWEETS = session.prepare("select * from tweets where username=? and tweet_id > ? order by tweet_id limit ?;")

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
    .setString(1, if(data.lastId.isEmpty) "" else data.lastId.get)
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
