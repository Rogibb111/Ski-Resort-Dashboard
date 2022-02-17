package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

object ResortSnapshotFactory {
    implicit val cardinalDirectionsRead: Reads[CardinalDirections] = Reads {
        case str if str.as[String] == North.toString() => JsSuccess(North)
        case str if str.as[String] == NorthWest.toString() => JsSuccess(NorthWest)
        case str if str.as[String] == NorthEast.toString() => JsSuccess(NorthEast)
        case str if str.as[String] == East.toString() => JsSuccess(East)
        case str if str.as[String] == South.toString() => JsSuccess(South)
        case str if str.as[String] == SouthEast.toString() => JsSuccess(SouthEast)
        case str if str.as[String] == SouthWest.toString() => JsSuccess(SouthWest)
        case str if str.as[String] == West.toString() => JsSuccess(West)
        case default => throw new Error("Tried to read string that wasn't a Cardinal Direction: "+ default.as[String])
    }
    implicit val resortDataRead: Reads[ResortData] = (
        (JsPath \ "dailySnow").read[Int] and
        (JsPath \ "baseDepth").read[Int] and
        (JsPath \ "temperature").read[Int] and
        (JsPath \ "windSpeed").read[Int] and
        (JsPath \ "windDir").read[CardinalDirections]
    ) (ResortData.apply _)

    def fromJson(data: String, resort:Resorts): ResortSnapshot = {
        val jsonData = Json.parse(data)
        val resortData = Json.fromJson[ResortData](jsonData)
        new ResortSnapshot(resort, resortData.get)
    }
}

final case class ResortData(dailySnow: Int, baseDepth: Int, temperature: Int, windSpeed: Int, windDir: CardinalDirections) 
final case class ResortSnapshot(resort: Resorts, resortData: ResortData)

object ResortsFactory {
    def fromString(resort: String): Resorts = {
        resort match {
            case ArapahoeBasin.databaseName => ArapahoeBasin
            case default => throw new Error("Tried to read string that wasn't a Resort")
        }
    }
}
sealed trait Resorts { val databaseName: String }
case object ArapahoeBasin extends Resorts {
    override def toString: String = "Arapahoe-Basin"
    override val databaseName = "ARAPAHOE_BASIN"
}