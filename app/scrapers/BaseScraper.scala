package scrapers

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import net.ruippeixotog.scalascraper.model.Element



import models._
import models.CardinalDirections
import models.CardinalDirectionsMapper
import scala.concurrent.Awaitable

object ScraperFactory {
  def initializeScraper(resort: Resorts): BaseScraper = {
    resort match {
      case ArapahoeBasin => new ABasinScraper
      
    }
  }
}

abstract class BaseScraper(resort: Resorts) {
  protected def scrape24HrSnowFall(): Int
  protected def scrapeBaseDepth(): Int

  protected val browser = JsoupBrowser()
  private val liveWeather = browser
    .get("https://www.snow-forecast.com/resorts/"+resort.toString()+"/") >> elementList(".live-snow__table tbody .live-snow__table-row")
  private val tcellMidLift = liveWeather.find(el => 
    (el >> element(".live-snow__table-row .live-snow__table-cell--elevation")).text == "Middle Lift:").get

  protected def scrapeTemperature(): Int = {
    return (tcellMidLift >> element(".temp")).text.toInt
  }

  protected def scrapeWindSpeed(): Int = {
    return (tcellMidLift >> element(".wind-icon__val")).text.toInt
  }

  protected def scrapeCardinalDirection(): CardinalDirections = {
    val transform = (tcellMidLift >> element(".wind-icon__arrow")).attr("transform")
    val degreeExtractor = "[a-z]+\\((\\d+)\\)".r
    val degreeExtractor(degreeStr) = transform
    return CardinalDirectionsMapper.fromDegree(degreeStr.toInt).get
  }

  def scrapeResort(): DatabaseSnapshot = {
    return new DatabaseSnapshot(
          scrape24HrSnowFall(),
          scrapeBaseDepth(),
          scrapeTemperature(),
          scrapeWindSpeed(),
          scrapeCardinalDirection()
      )
  }
}