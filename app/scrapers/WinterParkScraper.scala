package scrapers

import play.api.libs.ws.WSClient
import scala.concurrent.{ ExecutionContext, Await }
import scala.concurrent.duration._
import models.WinterPark
import play.api.libs.json.Json

class WinterParkScraper (ws: WSClient)(
implicit ec: ExecutionContext
) extends BaseScraper(WinterPark) {
    private val request = ws.url(WinterPark.scrapeUrl)
    private val snowReportResult = Await.result(request.get().map { response => 
        (response.json \ "SnowReport" \ "MidMountainArea")
    }, 5.second).getOrElse(null)

    override protected def scrape24HrSnowFall(): Int = (snowReportResult \ "Last24HoursIn").getOrElse(Json.parse("\"0\"")).as[String].toFloat.toInt

    override protected def scrapeBaseDepth(): Int = (snowReportResult \ "BaseIn").getOrElse(Json.parse("\"0\"")).as[String].toFloat.toInt
}
