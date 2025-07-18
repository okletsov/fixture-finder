package pageClasses;

import genericHelpers.EventMetadata;
import genericHelpers.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PopularBets {

    private static final Logger Log = LogManager.getLogger(PopularBets.class.getName());

    private final WebDriver driver;

    public PopularBets(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = "#popular-bets-full .icon__more")
    public WebElement btnMore;

    @FindBy(css = ".table-main td.h-text-left")
    public List<WebElement> events;

    public void clickMore() {

        List<WebElement> eventList = driver.findElements(By.cssSelector("#popular-bets-full .table-main__time"));
        int eventsBeforeMoreClicked = eventList.size();
        int eventsAfterMoreClicked;
        Log.debug(eventsBeforeMoreClicked + " events on a page before More button");

        Log.info("Clicking More Button...");

        Actions actions = new Actions(driver);
        actions.moveToElement(btnMore).perform();
        btnMore.click();
        do {
            eventList = driver.findElements(By.cssSelector("#popular-bets-full .table-main__time"));
            eventsAfterMoreClicked = eventList.size();
        } while (eventsAfterMoreClicked < eventsBeforeMoreClicked);

        Log.debug(eventsAfterMoreClicked + " events on a page after More button");
        Log.info("All events loaded. Events found: " + eventsAfterMoreClicked + "\n");
    }

    public List<EventMetadata> getPhaseOneEvents() {

        Properties prop = new Properties();
        BigDecimal homeOddsMin = prop.getHomeOddsMin();
        BigDecimal homeOddsMax = prop.getHomeOddsMax();
        int homeClicksLimit = prop.getHomeClicks();
        int homeClicksPctLimit = prop.getHomeClicksPct();

        List<EventMetadata> eventsMetadata = new ArrayList<>();

        for (WebElement el : events) {
            String eventName = el.findElement(By.xpath("./*[2]")).getText();
            BigDecimal homeOdds = new BigDecimal(el.findElement(By.xpath("following-sibling::*[1]/*[1]")).getText());
            BigDecimal drawOdds = new BigDecimal(el.findElement(By.xpath("following-sibling::*[2]/*[1]")).getText());
            BigDecimal awayOdds = new BigDecimal(el.findElement(By.xpath("following-sibling::*[3]/*[1]")).getText());
            int homeClicks = Integer.parseInt(el.findElement(By.xpath("following-sibling::*[4]")).getText());
            int homeClicksPct = Integer.parseInt(el.findElement(By.xpath("following-sibling::*[7]")).getText().replace("%", ""));
            String href = el.findElement(By.xpath("./*[2]")).getDomProperty("href");
            String id = href.substring(0, href.length() - 1).substring(href.substring(0, href.length() - 1).lastIndexOf('/') + 1);

            if(
                    homeOdds.compareTo(homeOddsMin) >= 0
                    && homeOdds.compareTo(homeOddsMax) <= 0
//                    && homeClicks >= homeClicksLimit
//                    && homeClicksPct >= homeClicksPctLimit
            ) {
                eventsMetadata.add(new EventMetadata(
                        eventName,
                        homeOdds,
                        drawOdds,
                        awayOdds,
                        homeClicks,
                        homeClicksPct,
                        href,
                        id
                ));
            }
        }

        return eventsMetadata;
    }

    public BigDecimal getHomeOddsById(String id) {
        String xpath = "(//button[contains(@onclick, '" + id + "')])[1]";
        List<WebElement> elements = driver.findElements(By.xpath(xpath));
        if (elements.isEmpty()) {
            return null;
        } else {
            String homeOdds = elements.get(0).getText();
            return new BigDecimal(homeOdds);
        }
    }
}
