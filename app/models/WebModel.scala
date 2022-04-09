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
            case Vail.databaseName => Vail
            case Keystone.databaseName => Keystone
            case Eldora.databaseName => Eldora
            case Copper.databaseName => Copper
            case WinterPark.databaseName => WinterPark
            case default => throw new Error("Tried to read string that wasn't a Resort")
        }
    }
    def fromNameString(resort: String): Resorts = {
        resort match {
            case resort if resort == ArapahoeBasin.toString() => ArapahoeBasin
            case resort if resort == Breckenridge.toString() => Breckenridge
            case resort if resort == BeaverCreek.toString() => BeaverCreek
            case resort if resort == Vail.toString() => Vail
            case resort if resort == Keystone.toString() => Keystone
            case resort if resort == Eldora.toString() => Eldora
            case resort if resort == Copper.toString() => Copper
            case resport if resort == WinterPark.toString() => WinterPark
            case default => throw new Error("Tried to read string that wasn't a Resort")
        }
    }
}
sealed trait Resorts { val databaseName: String; val scrapeUrl: String }
sealed trait PowdrResorts extends Resorts { val location_id: Int }
case object ArapahoeBasin extends Resorts {
    override def toString: String = "Arapahoe-Basin"
    override val databaseName = "ARAPAHOE_BASIN"
    override val scrapeUrl: String = "http://www.arapahoebasin.com"
}
case object Breckenridge extends Resorts {
    override def toString: String = "Breckenridge"
    override val databaseName: String = "BRECKENRIDGE"
    override val scrapeUrl: String = "https://www.breckenridge.com/api/PageApi/GetWeatherDataForHeader"
}
case object BeaverCreek extends Resorts {
    override def toString: String = "Beaver-Creek"
    override val databaseName: String = "BEAVERCREEK"
    override val scrapeUrl: String = "https://www.beavercreek.com/api/PageApi/GetWeatherDataForHeader"
}

case object Vail extends Resorts {
    override def toString: String = "Vail"
    override val databaseName: String = "VAIL"
    override val scrapeUrl: String = "https://www.vail.com/api/PageApi/GetWeatherDataForHeader"
}

case object Keystone extends Resorts {
    override def toString: String = "Keystone-Resort"
    override val databaseName: String = "KEYSTONE"
    override val scrapeUrl: String = "https://www.keystoneresort.com/api/PageApi/GetWeatherDataForHeader"   
}

case object Eldora extends PowdrResorts {
    override def toString: String = "Eldora-Mountain-Resort"
    override val databaseName: String = "ELDORA"
    override val scrapeUrl: String = "https://www.eldora.com/api/v1/dor/conditions"
    override val location_id: Int = 11
}

case object Copper extends PowdrResorts {
    override def toString: String = "Copper-Mountain"
    override val databaseName: String = "COPPER"
    override val scrapeUrl: String = "https://www.coppercolorado.com/api/v1/dor/conditions"
    override val location_id: Int = 7
}

case object WinterPark extends Resorts {
    override def toString: String = "Winter-Park"
    override val databaseName: String = "WINTERPARK"
    override val scrapeUrl: String = "https://mtnpowder.com/feed?resortId=5"
}