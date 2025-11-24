package pageClasses;

import genericHelpers.EventMetadata;
import genericHelpers.Properties;
import genericHelpers.SeleniumMethods;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class HomePage {

    private static final Logger Log = LogManager.getLogger(HomePage.class.getName());

    private final WebDriver driver;
    private final String eventCss = "[data-event-id]";

    public HomePage(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(id = "homepage-seo-content")
    public WebElement pageBottom;

    @FindBy(css = "[data-event-id]")
    public List<WebElement> events;

    @FindBy(css = ".list-tabs__item__calendarArrowRight")
    public WebElement nextDayBtn;

    private List<WebElement> getEventList() {
        return driver.findElements(By.cssSelector(eventCss));
    }

    public void clickNextDayBtn() {
        Actions actions = new Actions(driver);
        actions.moveToElement(nextDayBtn).perform();
        nextDayBtn.click();
        SeleniumMethods sm = new SeleniumMethods(driver);
        sm.waitForElement(By.cssSelector(eventCss), Duration.ofSeconds(5));
    }

    public void loadAllEvents() {

        int currentCount = getEventList().size();
        int previousCount;

        Log.info("Scrolling down...");

        do {
//            Scroll to the bottom of the page and wait
            Actions actions = new Actions(driver);
            actions.moveToElement(pageBottom).perform();
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            previousCount = currentCount;
            currentCount = getEventList().size();

        } while (previousCount != currentCount);

        Log.info("All events loaded. Events found: " + currentCount + "\n");
    }

    public List<EventMetadata> getPhaseOneEvents() {

        Properties prop = new Properties();
        BigDecimal homeOddsMin = prop.getHomeOddsMin();
        BigDecimal homeOddsMax = prop.getHomeOddsMax();

        List<EventMetadata> eventsMetadata = new ArrayList<>();

        for (WebElement el : events) {

//                Find event name
            String homeName = el.findElement(By.xpath(".//a//div[1]//p")).getText();
            String awayName = el.findElement(By.xpath(".//a//div[3]//p")).getText();
            String eventName = homeName + " - " + awayName;
            Log.debug("Inspecting event: " + eventName);

//            Skipping to the next event if current event is not a future event
            long eventDateTime = Long.parseLong(el.getDomAttribute("data-ts"));
            boolean isFutureEvent = Instant.ofEpochSecond(eventDateTime).isAfter(Instant.now());
            if (!isFutureEvent) { continue; }

//            Skipping to the next event if odds for current event don't exist
            List<WebElement> eventOdds = el.findElements(By.xpath(".//*[@data-oid][1]//button"));
            if (eventOdds.isEmpty()) {continue; }

//            Inspecting homeOdds
            BigDecimal homeOdds = new BigDecimal(el.findElement(By.xpath(".//*[@data-oid][1]//button")).getText());
            if(
                    homeOdds.compareTo(homeOddsMin) >= 0
                    && homeOdds.compareTo(homeOddsMax) <= 0
            ) {

//                Find the rest of metadata
                BigDecimal drawOdds = new BigDecimal(el.findElement(By.xpath(".//*[@data-oid][2]")).getText());
                BigDecimal awayOdds = new BigDecimal(el.findElement(By.xpath(".//*[@data-oid][3]")).getText());
                String href = el.findElement(By.xpath(".//a")).getDomProperty("href");
                String id = el.getDomAttribute("data-event-id");

//                Add event to the list
                eventsMetadata.add(new EventMetadata(
                        eventName,
                        homeOdds,
                        drawOdds,
                        awayOdds,
                        href,
                        id
                ));
            }
        }

        return eventsMetadata;
    }

    public BigDecimal getHomeOddsById(String id) {
        String xpath = "//*[@data-event-id='" + id + "']//*[@data-oid][1]//button";

        List<WebElement> elements = driver.findElements(By.xpath(xpath));
        if (elements.isEmpty()) {
            return null;
        } else {
            String homeOdds = elements.get(0).getText();
            return new BigDecimal(homeOdds);
        }
    }
}
