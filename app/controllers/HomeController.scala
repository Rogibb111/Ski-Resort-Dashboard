package controllers

import javax.inject._
import play.api.mvc._
import dao.ResortData
import models._
import scrapers.ABasinScraper
import collection.mutable.ArrayBuffer
import collection.mutable.Map
import scala.concurrent.Future
import scala.concurrent._
import ExecutionContext.Implicits.global 
import scrapers.ScraperFactory
import play.api.libs.ws.WSClient


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents, val resortData: ResortData, val ws: WSClient) extends BaseController {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action.async { implicit request: Request[AnyContent] =>
    resortData.getLatestSnapshotForAllResorts.map(
      rvArray => rvArray.map(f => ResortSnapshotFactory.resortSnapshotFromJson(f._1.asInstanceOf[String], ResortsFactory.fromDBString(f._2)))//ResortSnapshotFactory.fromJson(f(0).asInstanceOf[String], ta).asInstanceOf[ResortSnapshot]
    ).map(snapshotArray => Ok(views.html.index(snapshotArray)))
  }

  def resort(resort: Resorts) = Action.async { implicit request: Request[AnyContent] => 
    resortData.getAllSnapshotsForSingleResort(resort).map(
      dataArray => dataArray.map(v => ResortSnapshotFactory.resortDataSnapshotFromJson(v._1, v._2.toString()))
    ).map(dataArray => Ok(views.html.resort(dataArray, resort)))
  }

  def scrape() = Action.async { implicit request: Request[AnyContent] => 
    var resortDataMap: Map[Resorts, DatabaseSnapshot] = Map[Resorts, DatabaseSnapshot]()
    var resortFutureSeq: ArrayBuffer[Future[Unit]] = ArrayBuffer.empty
    
    resortFutureSeq.addOne(generateFuture(resortDataMap, ArapahoeBasin))
    resortFutureSeq.addOne(generateFuture(resortDataMap, Breckenridge))
    resortFutureSeq.addOne(generateFuture(resortDataMap, BeaverCreek))
    resortFutureSeq.addOne(generateFuture(resortDataMap, Vail))
    Future.sequence(resortFutureSeq).map(futureArray => {
      resortData.setSnapshotForResort(resortDataMap.toMap)
      Ok
    })
  }

  private def generateFuture(resortDataMap: Map[Resorts, DatabaseSnapshot], resort: Resorts): Future[Unit] = {
    Future {
      try {
        val scraper = ScraperFactory.initializeScraper(resort, ws)
        resortDataMap.put(resort, scraper.scrapeResort())
      } catch { case e: Exception => println(e.printStackTrace()) }
    }
  }
}
