package pageClasses;

import helpers.SeleniumMethods;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;
import java.util.List;

public class CommonElements {

    private static final Logger Log = LogManager.getLogger(CommonElements.class.getName());

    private final WebDriver driver;

    public CommonElements(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = "#onetrust-reject-all-handler")
    public WebElement btnRejectCookies;

    public void clickRejectCookies() {

        SeleniumMethods sm = new SeleniumMethods(driver);
        sm.waitForElement(btnRejectCookies, Duration.ofSeconds(5));
        btnRejectCookies.click();

    }

}
