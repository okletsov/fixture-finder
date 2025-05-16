package helpers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class BrowserDriver {

    private static final Logger Log = LogManager.getLogger(BrowserDriver.class.getName());

    private ChromeDriver driver;

    public BrowserDriver() {
        Properties prop = new Properties();
        String headless = prop.getHeadless();
        setChromedriverPath();
        System.setProperty("polyglot.engine.WarnInterpreterOnly", "false");

        if (headless.equals("true")){
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");
            options.addArguments("--window-size=1920,1080");
            this.driver = new ChromeDriver(options);
        } else if (headless.equals("false")){
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--window-size=1920,1080");
            this.driver = new ChromeDriver();
        }
        assert driver != null;
    }

    private void setChromedriverPath() {
        String osArch = System.getProperty("os.arch");
        if (osArch.equals("aarch64")) {
            String chromedriverPath = "/usr/bin/chromedriver";
            System.setProperty("webdriver.chrome.driver", chromedriverPath);
            Log.info("found " + osArch + " architecture, manually setting chromedriver path to: " + chromedriverPath);
        } else {
            Log.info("Using system's default chromedriver path");
        }
    }

    public ChromeDriver getDriver() {
        return driver;
    }

}