package repositories

import app.AppConfig
import com.datastax.oss.driver.api.core.{CqlSession, DefaultConsistencyLevel}
import models.{FollowRequestData, UserData}
import play.api.Configuration

import javax.inject.{Inject, Singleton}
import scala.compat.java8.FutureConverters.CompletionStageOps
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserRepository @Inject() (protected val config: Configuration)(implicit val ec: ExecutionContext)  {

  val appConfig = config.get[AppConfig]("app.config")

  val session = CqlSession
    .builder()
    //.addContactPoint(new InetSocketAddress(Config.CASSANDRA_HOST, Config.CASSANDRA_PORT))
    //.withConfigLoader(loader)
    //.withLocalDatacenter(Config.DC)
    .withKeyspace(appConfig.keyspace)
    .build()

  protected val INSERT_USER = session.prepare("insert into users(username, password, email, signup_tmp, id) values (?,?,?,?,?) if not exists;")
  protected val GET_USER_BY_USERNAME = session.prepare("select * from users where username=? and password=?;")

  protected val INSERT_FOLLOWER = session.prepare("insert into following(follower, followee, tmp) values(?,?,?);")

  def insert(user: UserData): Future[Boolean] = {
    session.executeAsync(INSERT_USER.bind()
    .setString(0, user.username)
    .setString(1, play.api.libs.Codecs.sha1(user.password))
    .setString(2, user.email)
    .setLong(3, user.signup_tmp)
    .setString(4, user.id)
    .setConsistencyLevel(DefaultConsistencyLevel.LOCAL_QUORUM)).toScala.map(_.wasApplied())
  }

  def authByUsername(username: String, hash: String): Future[Option[UserData]] = {
    session.executeAsync(GET_USER_BY_USERNAME.bind().setString(0, username).setString(1, hash)).toScala.map { rs =>
      val one = rs.one()
      if(one == null) {
        None
      } else {
        Some(UserData(one.getString("username"), one.getString("password"), one.getString("email"),
          one.getString("id"), one.getLong("signup_tmp")))
      }
    }
  }

  def follow(request: FollowRequestData): Future[Boolean] = {
    session.executeAsync(INSERT_FOLLOWER.bind()
    .setString(0, request.follower)
    .setString(1, request.followee)
    .setLong(2, request.tmp)).toScala.map(_.wasApplied())
  }

}
