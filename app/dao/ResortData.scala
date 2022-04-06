package dao

import com.google.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.{ ExecutionContext, Future }
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import javax.inject.Singleton
import scala.util.{ Success, Failure }
import java.util.Date
import java.sql.Timestamp
import slick.lifted.ProvenShape

import models._



/* -----------Verbiage-----------
   Snapshot: A single entry in the database. Contains base depth and 24 hour snowfall for a single resort at a single time
 */
@Singleton
class ResortData @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(
    implicit ec: ExecutionContext
) extends HasDatabaseConfigProvider[JdbcProfile] {

        //----- DataBase Setup and Migrations -----\\
        private val resortData = TableQuery[ResortDataSchema]
        private def columnCheckandAdd(resort: Resorts) = {
            val resortDBName = resort.databaseName
            sql"""ALTER TABLE "RESORT_DATA" ADD COLUMN IF NOT EXISTS "#$resortDBName" VARCHAR""".asUpdate
        }
        
        private val setup = DBIO.seq(
            resortData.schema.createIfNotExists,
            columnCheckandAdd(Breckenridge),
            columnCheckandAdd(BeaverCreek)
        )

        db.run(setup).onComplete({
            case Success(value) => println("Success!")
            case Failure(exception) => println(exception.printStackTrace())
        })
        
        // ----- Database Queries ----- \\
        def getLatestSnapshotForAllResorts: (Future[Array[(Any, String)]]) = {
            val q = resortData.sortBy(_.created.desc).take(1)
            val tableNames = resortData.baseTableRow.create_*.map(_.name).toArray
            var rowValuesFuture: Future[(String, String, String,  Timestamp)] = db.run(q.result).map(_.last)
            rowValuesFuture.map(rv => rv.productIterator.toArray.dropRight(1).zip(tableNames))
        }

        def getAllSnapshotsForSingleResort(resort: Resorts): Future[Array[(String, Timestamp)]] = {
            val resortDBName = resort.databaseName
            val q = sql"""select "#$resortDBName", "CREATED" FROM "RESORT_DATA" order by "CREATED"""".as[(String, Timestamp)].map(_.toArray)
            db.run(q)
        }

        def setSnapshotForResort(databaseSnapshots: Map[Resorts, DatabaseSnapshot]): Unit = {
            val insertAction = DBIO.seq(
                resortData += (getResortSnapshot(ArapahoeBasin, databaseSnapshots).toJson(),
                 getResortSnapshot(Breckenridge, databaseSnapshots).toJson(), 
                 getResortSnapshot(BeaverCreek, databaseSnapshots).toJson(),
                 new java.sql.Timestamp(new Date().getTime()))
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

        private class ResortDataSchema(tag: Tag) extends Table[(String, String, String, Timestamp)](tag, "RESORT_DATA") {
            def arapahoeBasin = column[String](ArapahoeBasin.databaseName)
            def breckenridge = column[String](Breckenridge.databaseName)
            def beaverCreek = column[String](BeaverCreek.databaseName)
            def created = column[Timestamp]("CREATED")
            def * : ProvenShape[(String, String, String, Timestamp)] = (arapahoeBasin, breckenridge, beaverCreek, created)
        }
    }