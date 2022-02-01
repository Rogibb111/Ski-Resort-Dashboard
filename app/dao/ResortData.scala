package dao

import com.google.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext
import play.api.mvc.AbstractController
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import java.util.Date
import javax.inject.Singleton
import scala.util.Success
import scala.util.Failure
import java.sql.Timestamp
import slick.lifted.ProvenShape
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import slick.jdbc.meta.MTable
import org.joda.time.Seconds

import models._



/* -----------Verbiage-----------
   Snapshot: A single entry in the database. Contains base depth and 24 hour snowfall for a single resort at a single time
 */
@Singleton
class ResortData @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(
    implicit ec: ExecutionContext
) extends HasDatabaseConfigProvider[JdbcProfile] {

        private val resortData = TableQuery[ResortDataSchema]
        private val setup = DBIO.seq(
            resortData.schema.createIfNotExists,
        )

        db.run(setup).onComplete({
            case Success(value) => println("Success!")
            case Failure(exception) => println(exception.printStackTrace())
        })

        def getAllResortSnapshots(): Unit = {
            db.run(resortData.result).map(_.foreach {
                case (arapahoeBasin, timestamp) => 
                    println(" " + arapahoeBasin + "\t" + timestamp)
            })
        }

        def setSnapshotForResort(databaseSnapshots: Map[Resorts, DatabaseSnapshot]): Unit = {
            val insertAction = DBIO.seq(
                
                resortData += (getResortSnapshot(ArapahoeBasin, databaseSnapshots).toJson(), new java.sql.Timestamp(new Date().getTime()))
            )
            db.run(insertAction).onComplete({
                case Success(value) => println("Set snapshot success!")
                case Failure(exception) => println(exception.printStackTrace())
            })
        }

        @throws(classOf[Exception])
        private def getResortSnapshot(resort: Resorts, databaseSnapshots: Map[Resorts, DatabaseSnapshot]): DatabaseSnapshot = {
            val snapshotOption = databaseSnapshots.get(resort)
            if (snapshotOption.isEmpty) {
                throw new Exception("Resort " + resort + "wasn't added to the database snapshots Map for storage. Something maybe wrong with the data scraping for said resort")
            }
            return snapshotOption.get
        }

        private class ResortDataSchema(tag: Tag) extends Table[(String, Timestamp)](tag, "RESORT_DATA") {
            def arapahoeBasin = column[String]("ARAPAHOE_BASIN")
            def created = column[Timestamp]("CREATED")
            def * : ProvenShape[(String, Timestamp)] = (arapahoeBasin, created)
        }
    }