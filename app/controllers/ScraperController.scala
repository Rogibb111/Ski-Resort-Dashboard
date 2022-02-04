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
import play.api.libs.json.Json
import play.api.libs.json.Writes

import dao.ResortData
import models._

@Singleton
class ScraperController @Inject()(val controllerComponents: ControllerComponents, val dao: ResortData) extends BaseController {
    def index() = Action { implicit request: Request[AnyContent] =>
        val browser = JsoupBrowser()
        val conditionsLabels = browser.get("http://www.arapahoebasin.com") >> elementList(".ab-condition_sub")
        // Get the value for 24 Hour Snowfall
        val dailySnowLabel = conditionsLabels.filter(el => el.text == "Past 24 Hrs")(0)
        val dailySnowString = dailySnowLabel.siblings.find(el => el.attr("class") == "ab-condition_value").get.text
        val dailySnowVal = dailySnowString.substring(0, dailySnowString.length()-1).toIntOption.getOrElse(0)
        // Get the value for the height of the snow base
        val baseLabel = conditionsLabels.filter(el => el.text == "Base")(0)
        val baseString = baseLabel.siblings.find(el => el.attr("class") == "ab-condition_value").get.text
        val baseVal = baseString.substring(0, baseString.length()-1).toIntOption.getOrElse(0)


        val liveWeather = browser.get("https://www.snow-forecast.com/resorts/Arapahoe-Basin/") >> elementList(".live-snow__table tbody .live-snow__table-row")
        val tcellMidLift = liveWeather.find(el => (el >> element(".live-snow__table-row .live-snow__table-cell--elevation")).text == "Middle Lift:").get
        val temperature = (tcellMidLift >> element(".temp")).text.toInt
        val windSpd = (tcellMidLift >> element(".wind-icon__val")).text.toInt
        val transform = (tcellMidLift >> element(".wind-icon__arrow")).attr("transform")
        val degreeExtractor = "[a-z]+\\((\\d+)\\)".r
        val degreeExtractor(degreeStr) = transform
        val cardinalDir = CardinalDirectionsMapper.fromDegree(degreeStr.toInt)

        


        
        val databaseSnapshots: Map[Resorts, DatabaseSnapshot] = Map(ArapahoeBasin -> new DatabaseSnapshot(dailySnowVal, baseVal, temperature, windSpd, North))
        dao.setSnapshotForResort(databaseSnapshots)

        Ok("Scraping Finished")
    }
}
