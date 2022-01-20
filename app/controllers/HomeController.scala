package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.ws._
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    val browser = JsoupBrowser()
    val doc = browser.get("http://www.arapahoebasin.com");
    val items = doc >> elementList(".ab-condition");
    Console.println(items.map(_ >> allText("div")))
    Ok(views.html.index())
  }
}
