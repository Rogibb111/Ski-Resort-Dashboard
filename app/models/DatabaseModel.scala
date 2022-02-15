package models

import play.api.libs.json.Json
import play.api.libs.json.Writes
import play.api.libs.json.JsValue
import play.api.libs.json.JsString

final case class DatabaseSnapshot(dailySnow: Int, baseDepth: Int, temperature: Int, windSpeed: Int, windDir: CardinalDirections) {
    implicit val dirWrites = new Writes[CardinalDirections] {
        def writes(dir: CardinalDirections): JsValue = new JsString(dir.toString())
    }
    implicit val writes = Json.writes[DatabaseSnapshot]
    
    def toJson(): String = {
        return Json.toJson(this).toString()
    }
}