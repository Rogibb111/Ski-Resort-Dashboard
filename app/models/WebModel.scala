package models

import play.api.libs.json._
import play.api.libs.functional.syntax._
import models.North

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

    def resortSnapshotFromJson(data: String, resort:Resorts): ResortSnapshot = {
        val jsonData = Json.parse(data)
        val resortData = Json.fromJson[ResortData](jsonData)
        new ResortSnapshot(resort, resortData.get)
    }

    def resortDataSnapshotFromJson(data: String, timestamp: String): ResortDataSnapshot = {
            val dataOption = Option(data)

            if (dataOption.getOrElse("").isEmpty()) {
                val resortData = new ResortData(0,0,0,0,North)
                new ResortDataSnapshot("", resortData)
            } else {
                val jsonData = Json.parse(data)
                val resortData = Json.fromJson[ResortData](jsonData)
                new ResortDataSnapshot(timestamp, resortData.get)
            }
    }
}

final case class ResortData(dailySnow: Int, baseDepth: Int, temperature: Int, windSpeed: Int, windDir: CardinalDirections) 
final case class ResortSnapshot(resort: Resorts, resortData: ResortData)
final case class ResortDataSnapshot(timestamp: String, resortData: ResortData)

object ResortsFactory {
    def fromDBString(resort: String): Resorts = {
        resort match {
            case ArapahoeBasin.databaseName => ArapahoeBasin
            case Breckenridge.databaseName => Breckenridge
            case BeaverCreek.databaseName => BeaverCreek
            case default => throw new Error("Tried to read string that wasn't a Resort")
        }
    }
    def fromNameString(resort: String): Resorts = {
        resort match {
            case resort if resort == ArapahoeBasin.toString() => ArapahoeBasin
            case resort if resort == Breckenridge.toString() => Breckenridge
            case resort if resort == BeaverCreek.toString() => BeaverCreek
            case default => throw new Error("Tried to read string that wasn't a Resort")
        }
    }
}
sealed trait Resorts { val databaseName: String }
sealed trait EpicResorts extends Resorts { val scrapeUrl: String }
case object ArapahoeBasin extends Resorts {
    override def toString: String = "Arapahoe-Basin"
    override val databaseName = "ARAPAHOE_BASIN"
}
case object Breckenridge extends EpicResorts {
    override def toString: String = "Breckenridge"
    override val databaseName: String = "BRECKENRIDGE"
    override val scrapeUrl: String = "https://www.breckenridge.com/api/PageApi/GetWeatherDataForHeader"
}
case object BeaverCreek extends EpicResorts {
    override def toString: String = "Beaver-Creek"
    override val databaseName: String = "BEAVERCREEK"
    override val scrapeUrl: String = "https://www.beavercreek.com/api/PageApi/GetWeatherDataForHeader"
    
}