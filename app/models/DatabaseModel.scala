package models

import play.api.libs.json.Json

final case class DatabaseSnapshot(dailySnow: Int, baseDepth: Int) {
    def toJson(): String = {
        implicit val writes = Json.writes[DatabaseSnapshot]
        return Json.toJson(this).toString()
    }
}