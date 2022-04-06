package scrapers

import models.Breckenridge
import javax.inject.Inject
import play.api.libs.ws._
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Success
import scala.util.Failure
import play.api.libs.json._
import scala.concurrent.Await
import scala.concurrent.duration._
import models.EpicResorts


case class SnowMeasurement (Inches: String, Centimeters: String)
case class SnowResult (Depth: SnowMeasurement, Description: String)


class EpicScraper (ws: WSClient, resort: EpicResorts)(
    implicit ec: ExecutionContext
) extends BaseScraper(resort)  {
  implicit val SnowMeasurementReads = Json.reads[SnowMeasurement]
  implicit val SnowResultReads = Json.reads[SnowResult]

  private val request = ws.url(resort.scrapeUrl)
  private val snowReportResult: Set[SnowResult] = Await.result(request.get().map { response =>
    (response.json \ "SnowReportSections").validate[Set[SnowResult]]
  }, 5.second).get

  // val snowReport = request.get().toCompletableFuture().get().asJson.get("SnowReportSections").asInstanceOf[ArrayNode];

  protected def scrape24HrSnowFall(): Int = {
    return snowReportResult.find(reportItem => reportItem.Description.contains("24 Hour")).get.Depth.Inches.toInt;
  }
  
  protected def scrapeBaseDepth(): Int = {
    return snowReportResult.find(reportItem => reportItem.Description.contains("Base")).get.Depth.Inches.toInt;
  }
  
}
