package scrapers

import play.api.libs.ws.WSClient
import models.Resorts
import scala.concurrent.{ ExecutionContext, Await }
import scala.concurrent.duration._
import play.api.libs.json._
import models.PowdrResorts

class PowdrScraper (ws: WSClient, resort: PowdrResorts)(
    implicit ec: ExecutionContext
) extends BaseScraper(resort) {
    case class SnowAmount(amount: Int, duration: String)
    case class SnowResult (items:Seq[SnowAmount], location_id: Int)

    implicit val SnowAmountReads = Json.reads[SnowAmount]
    implicit val SnowResultReads = Json.reads[SnowResult]

    private val request = ws.url(resort.scrapeUrl)
    private val snowReportResult: SnowResult = Await.result(request.get().map { response =>
        (response.json \ "snowReport").validate[Set[SnowResult]]
    }, 5.seconds).get.find(report => report.location_id == resort.location_id).get

    override protected def scrape24HrSnowFall(): Int = {
        snowReportResult.items.find(item => item.duration == "24 Hours").get.amount
    }
    
    override protected def scrapeBaseDepth(): Int = {
        snowReportResult.items.find(item => item.duration == "base-depth").get.amount
    }
    
    
}
