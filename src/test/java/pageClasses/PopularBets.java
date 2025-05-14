package pageClasses;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class PopularBets {

    private static final Logger Log = LogManager.getLogger(PopularBets.class.getName());

    private final WebDriver driver;

    public PopularBets(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

}
