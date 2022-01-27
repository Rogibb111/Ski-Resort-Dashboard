package models

final case class ResortSnapshot(resort: Resorts, dailySnow: Int, baseDepth: Int)

sealed trait Resorts
case object ArapahoeBasin extends Resorts