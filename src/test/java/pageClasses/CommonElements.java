package pageClasses;

import genericHelpers.SeleniumMethods;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;

public class CommonElements {

    private static final Logger Log = LogManager.getLogger(CommonElements.class.getName());

    private final WebDriver driver;

    public CommonElements(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void clickRejectCookies() {

        String css = "#onetrust-reject-all-handler";
        SeleniumMethods sm = new SeleniumMethods(driver);
        sm.waitForElement(By.cssSelector(css), Duration.ofSeconds(5));
        driver.findElement(By.cssSelector(css)).click();
        sm.waitForElementInvisibility(By.cssSelector(css), Duration.ofSeconds(5));
    }
}
