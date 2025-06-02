package pageClasses;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class EventDetails {

    private static final Logger Log = LogManager.getLogger(EventDetails.class.getName());

    private final WebDriver driver;

    public EventDetails(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = ".breadcrumb__ul > :nth-child(2) a")
    public WebElement sport;

    @FindBy(css = ".breadcrumb__ul > :nth-child(3) a")
    public WebElement country;

    @FindBy(css = ".breadcrumb__ul > :nth-child(4) a")
    public WebElement league;

    @FindBy(id = "isFinished")
    public WebElement isFinished;

    @FindBy(id = "js-score")
    public WebElement score;

    public String getSport() {
        return sport.getText();
    }

    public String getCountry() {
        return country.getText();
    }

    public String getLeague() {
        return league.getText();
    }

    public String getScore() {
        if (isFinished.getDomAttribute("value").equals("1")) {
            return score.getText();
        } else {
            return null;
        }
    }
}
