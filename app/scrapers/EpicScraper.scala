package scrapers

import javax.inject.Inject
import play.api.libs.ws._
import scala.concurrent.{ ExecutionContext, Future, Await }
import scala.util.{ Success, Failure }
import play.api.libs.json._
import scala.concurrent.duration._
import models.Resorts


class EpicScraper (ws: WSClient, resort: Resorts)(
    implicit ec: ExecutionContext
) extends BaseScraper(resort)  {
    case class SnowMeasurement (Inches: String, Centimeters: String)
    case class SnowResult (Depth: SnowMeasurement, Description: String)
    private val default = SnowResult(SnowMeasurement("0", ""), "")

    implicit val SnowMeasurementReads = Json.reads[SnowMeasurement]
    implicit val SnowResultReads = Json.reads[SnowResult]

    private val request = ws.url(resort.scrapeUrl)
    private val snowReportResult: Set[SnowResult] = Await.result(request.get().map { response =>
        (response.json \ "SnowReportSections").validate[Set[SnowResult]]
    }, 5.second).getOrElse(Set(default))

    protected def scrape24HrSnowFall(): Int = {
        return snowReportResult.find(reportItem => reportItem.Description.contains("24 Hour")).getOrElse(default).Depth.Inches.toInt;
    }
    
    protected def scrapeBaseDepth(): Int = {
        return snowReportResult.find(reportItem => reportItem.Description.contains("Base")).getOrElse(default).Depth.Inches.toInt;
    }
  
}
