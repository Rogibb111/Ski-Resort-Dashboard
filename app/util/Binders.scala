package util

import java.util.UUID
import play.api.mvc.PathBindable
import models.Resorts
import models.ResortsFactory

object Binders {
   implicit def resortsPathBinder = new PathBindable[Resorts] {
      override def bind(key: String, value: String): Either[String, Resorts] = {
         try {
            Right(ResortsFactory.fromNameString(value))
         } catch {
            case e: Throwable => Left("This is not a valid resort")
         }
      }
      override def unbind(key: String, value: Resorts): String = value.toString
   }
}