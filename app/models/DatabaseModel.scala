package models

import play.api.libs.json.Json

final case class DatabaseSnapshot(dailySnow: Int, baseDepth: Int, temperature: Int, windSpeed: Int, windDir: CardinalDirections) {
    implicit val nWrites = Json.writes[North.type]
    implicit val neWrites = Json.writes[NorthEast.type]
    implicit val nwWrites = Json.writes[NorthWest.type]
    implicit val sWrites = Json.writes[South.type]
    implicit val swWrites = Json.writes[SouthWest.type]
    implicit val seWrites = Json.writes[SouthEast.type]
    implicit val eWrites = Json.writes[East.type]
    implicit val wWrites = Json.writes[West.type]
    implicit val dirWrites = Json.writes[CardinalDirections]
    implicit val writes = Json.writes[DatabaseSnapshot]
    
    def toJson(): String = {
        return Json.toJson(this).toString()
    }
}