package scrapers

import play.api.libs.ws.WSClient
import models.Resorts
import scala.concurrent.{ ExecutionContext, Await }
import scala.concurrent.duration._
import play.api.libs.json._

class PowdrScraper (ws: WSClient)(
    implicit ec: ExecutionContext
) {
    case class SnowAmount(amount: Int, duration: String)
    case class SnowResult (items:Seq[SnowAmount], location_id: Int)

    implicit val SnowAmountReads = Json.reads[SnowAmount]
    implicit val SnowResultReads = Json.reads[SnowResult]

    private val request = ws.url("https://www.eldora.com/api/v1/dor/conditions")
    private val snowReportResult: SnowResult = Await.result(request.get().map { response =>
        (response.json \ "snowReport").validate[Set[SnowResult]]
    }, 5.seconds).get.find(report => report.location_id == 11).get

    println(snowReportResult)
    
}
