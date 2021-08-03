package repositories

import app.AppConfig
import com.datastax.oss.driver.api.core.{CqlSession, DefaultConsistencyLevel}
import models.{FollowRequestData, UserData}
import play.api.Configuration

import java.nio.file.Paths
import javax.inject.{Inject, Singleton}
import scala.compat.java8.FutureConverters.CompletionStageOps
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserRepository @Inject() (protected val config: Configuration)(implicit val ec: ExecutionContext)  {

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
