package dao

import com.google.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import java.util.Date
import javax.inject.Singleton
import scala.util.Success
import scala.util.Failure
import java.sql.Timestamp
import slick.lifted.ProvenShape

import models._
import scala.concurrent.Future



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

        def getLatestSnapshotForAllResorts: (Future[Array[(Any, String)]]) = {
            val q = resortData.sortBy(_.created.desc).take(1)
            val tableNames = resortData.baseTableRow.create_*.map(_.name).toArray
            var rowValuesFuture: Future[(String, Timestamp)] = db.run(q.result).map(_.last)
            rowValuesFuture.map(rv => rv.productIterator.toArray.dropRight(1).zip(tableNames))
        }

        def getAllSnapshotsForSingleResort(resort: Resorts): Future[Array[(String, Timestamp)]] = {
            val resortDBName = resort.databaseName
            val q = sql"""select "#$resortDBName", "CREATED" FROM "RESORT_DATA" order by "CREATED"""".as[(String, Timestamp)].map(_.toArray)
            db.run(q)
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
            def arapahoeBasin = column[String](ArapahoeBasin.databaseName)
            def created = column[Timestamp]("CREATED")
            def * : ProvenShape[(String, Timestamp)] = (arapahoeBasin, created)
        }
    }