package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.ws._
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import net.ruippeixotog.scalascraper.model.Element

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
    val conditionsLabels = doc >> elementList(".ab-condition_sub")
    // Get the value for 24 Hour Snowfall
    val dailySnowLabel = conditionsLabels.filter(el => el.text == "Past 24 Hrs")(0)
    val dailySnowString = dailySnowLabel.siblings.find(el => el.attr("class") == "ab-condition_value").get.text
    val dailySnowVal = dailySnowString.substring(0, dailySnowString.length()-1).toInt
    // Get the value for the height of the snow base
    val baseLabel = conditionsLabels.filter(el => el.text == "Base")(0)
    val baseString = baseLabel.siblings.find(el => el.attr("class") == "ab-condition_value").get.text
    val baseVal = baseString.substring(0, baseString.length()-1).toInt
    Console.println(dailySnowVal)
    Console.println(baseVal)
    Ok(views.html.index())
  }
}
