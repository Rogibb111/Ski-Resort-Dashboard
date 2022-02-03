package models

import javax.smartcardio.Card

sealed trait CardinalDirections {
    def fromString(value: String): Option[CardinalDirections] = value match {
        case "North" => Some(North)
        case "NorthEast" => Some(NorthEast)
        case "East" => Some(East)
        case "SouthEast" => Some(SouthEast)
        case "South" => Some(South)
        case "SouthWest" => Some(SouthWest)
        case "West" => Some(West)
        case "NorthWest" => Some(NorthWest)
        case _ => None
    }
}
case object North extends CardinalDirections
case object NorthEast extends CardinalDirections
case object East extends CardinalDirections
case object SouthEast extends CardinalDirections
case object South extends CardinalDirections
case object SouthWest extends CardinalDirections
case object West extends CardinalDirections
case object NorthWest extends CardinalDirections
