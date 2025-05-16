package pageClasses;

import helpers.SeleniumMethods;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

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
        Log.info("All events loaded. Events found: " + eventsAfterMoreClicked);
    }

}
