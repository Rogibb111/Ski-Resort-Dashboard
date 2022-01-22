package dataModels

import scalaz.std.int
import dataModels.Mountains

final case class MountainDataPoint(mountain: Mountains, dailySnow: Int, baseDepth: Int)