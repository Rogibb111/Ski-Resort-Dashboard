package scrapers

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import net.ruippeixotog.scalascraper.model.Element

import models._

class ABasinScraper extends BaseScraper(ArapahoeBasin)  {
    val conditionsLabels = browser.get(ArapahoeBasin.scrapeUrl) >> elementList(".ab-condition_sub")
    private val jsoupElementDefault = new org.jsoup.nodes.Element("default")
    private val elementDefault = JsoupBrowser.JsoupElement(jsoupElementDefault)

    protected override def scrape24HrSnowFall(): Int = {
        val dailySnowLabel = conditionsLabels.filter(el => el.text == "Past 24 Hrs") match {
            case filteredList if filteredList.isEmpty => elementDefault
            case filteredList => filteredList(0) 
        }
        val dailySnowString = dailySnowLabel.siblings.find(el => el.attr("class") == "ab-condition_value").getOrElse(elementDefault).text
        dailySnowString match {
            case str if str.isEmpty() => 0
            case str => str.substring(0, dailySnowString.length()-1).toIntOption.getOrElse(0)
        }
    }

    protected override def scrapeBaseDepth(): Int = {
        val baseLabel = conditionsLabels.filter(el => el.text == "Base") match {
            case filteredList if filteredList.isEmpty => elementDefault
            case filteredList => filteredList(0) 
        }
        val baseString = baseLabel.siblings.find(el => el.attr("class") == "ab-condition_value").getOrElse(elementDefault).text
        baseString match {
            case str if str.isEmpty() => 0
            case str => str.substring(0, baseString.length()-1).toIntOption.getOrElse(0)
        }
    }
}