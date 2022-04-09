package scrapers

import play.api.libs.ws.WSClient
import scala.concurrent.{ ExecutionContext, Await }
import scala.concurrent.duration._
import models.WinterPark

class WinterParkScraper (ws: WSClient)(
implicit ec: ExecutionContext
) extends BaseScraper(WinterPark) {



    private val request = ws.url(WinterPark.scrapeUrl)
    private val snowReportResult = Await.result(request.get().map { response => 
        (response.json \ "SnowReport" \ "MidMountainArea")
    }, 5.second).get

    override protected def scrape24HrSnowFall(): Int = (snowReportResult \ "Last24HoursIn").get.as[String].toFloat.toInt

    override protected def scrapeBaseDepth(): Int = (snowReportResult \ "BaseIn").get.as[String].toFloat.toInt
}
