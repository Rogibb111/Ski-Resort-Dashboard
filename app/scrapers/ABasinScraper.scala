package scrapers

import models.Resorts
import models.ArapahoeBasin

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import net.ruippeixotog.scalascraper.model.Element
import models.DatabaseSnapshot

class ABasinScraper extends BaseScraper(ArapahoeBasin)  {
    val conditionsLabels = browser.get("http://www.arapahoebasin.com") >> elementList(".ab-condition_sub")

    private def scrape24HrSnowFall(): Int = {
        val dailySnowLabel = conditionsLabels.filter(el => el.text == "Past 24 Hrs")(0)
        val dailySnowString = dailySnowLabel.siblings.find(el => el.attr("class") == "ab-condition_value").get.text
        return dailySnowString.substring(0, dailySnowString.length()-1).toIntOption.getOrElse(0)
    }

    private def scrapeBaseDepth(): Int = {
        val baseLabel = conditionsLabels.filter(el => el.text == "Base")(0)
        val baseString = baseLabel.siblings.find(el => el.attr("class") == "ab-condition_value").get.text
        return baseString.substring(0, baseString.length()-1).toIntOption.getOrElse(0)
    }

    override def scrapeResort: DatabaseSnapshot = {
        return new DatabaseSnapshot(
            scrape24HrSnowFall(),
            scrapeBaseDepth(),
            scrapeTemperature(),
            scrapeWindSpeed(),
            scrapeCardinalDirection()
        )
    }
}