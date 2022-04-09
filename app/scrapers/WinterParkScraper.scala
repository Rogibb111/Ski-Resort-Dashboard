package scrapers

import play.api.libs.ws.WSClient
import scala.concurrent.{ ExecutionContext, Await }
import scala.concurrent.duration._

class WinterParkScraper (ws: WSClient)(
implicit ec: ExecutionContext
) extends {
    private val request = ws.url("https://mtnpowder.com/feed?resortId=5")
    private val snowReportResult = Await.result(request.get().map { response => 
        (response.json \ "SnowReport" \ "MidMountainArea")
    }, 5.second).get
    println(snowReportResult)
    println((snowReportResult \ "BaseIn").get.as[String].toFloat.toInt)
    println((snowReportResult \ "Last24HoursIn").get.as[String].toFloat.toInt)
}
