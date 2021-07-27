package controllers

import play.api.mvc._

import javax.inject._
import scala.concurrent.Future
import play.api.libs.json._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class AccountController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  case class UserData(username: String)
  implicit val userDataFormat = Json.reads[UserData]

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action.async { implicit request: Request[AnyContent] =>

    val json = request.body.asJson

    if(json.isEmpty){
       Future.successful(NotFound("User data not found!"))
    } else {

      println(json.get.as[UserData])

      Future.successful(Ok(json.get))
    }
  }
}
