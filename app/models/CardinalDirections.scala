package models

import com.fasterxml.jackson.module.scala.deser.overrides

sealed trait CardinalDirections
case object North extends CardinalDirections {
    override def toString: String = "North"
}
case object NorthEast extends CardinalDirections {
    override def toString: String = "NorthEast"
}
case object East extends CardinalDirections {
    override def toString: String = "East"
}
case object SouthEast extends CardinalDirections {
    override def toString: String = "SouthEast"
}
case object South extends CardinalDirections {
    override def toString: String = "South"
}
case object SouthWest extends CardinalDirections {
    override def toString: String = "SouthWest"
}
case object West extends CardinalDirections {
    override def toString: String = "West"
}
case object NorthWest extends CardinalDirections {
    override def toString: String = "NorthWest"
}

object CardinalDirectionsMapper {
    def fromDegree(value: Int): Option[CardinalDirections] = value match {
        case x if x >= 337.5 || x < 22.5 => Some(North) // 0 degrees in center of range
        case x if x >= 22.5 || x < 67.5 => Some(NorthEast) // 45 degrees in center of range
        case x if x >= 67.5 || x < 112.5 => Some(East) // 90 degrees in center of range
        case x if x >= 112.5 || x < 157.5 => Some(SouthEast) // 135 degrees in center of range
        case x if x >= 157.5 || x < 202.5 => Some(South) // 180 degrees in center of range
        case x if x >= 202.5 || x < 247.5 => Some(SouthWest) // 225 degrees in center of range
        case x if x >= 247.5 || x < 292.5 => Some(West) // 270 degrees in center of range
        case x if x >= 292.5 || x < 337.5 => Some(NorthWest) // 315 degrees in center of range
        case _ => None
    }
}
