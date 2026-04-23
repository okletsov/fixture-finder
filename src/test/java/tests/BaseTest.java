package tests;

import databaseHelpers.DatabaseOperations;
import genericHelpers.BrowserDriver;
import genericHelpers.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import pageClasses.CommonElements;
import pageClasses.HomePage;

import java.sql.Connection;

public class BaseTest {

    private static final Logger Log = LogManager.getLogger(BaseTest.class.getName());

    protected final DatabaseOperations dbOp = new DatabaseOperations();
    protected Connection conn = null;
    protected ChromeDriver driver;
    protected Properties prop;
    protected HomePage homePage;
    protected CommonElements ce;

    @BeforeTest
    public void setUp() {
        // Register the shutdown hook for fallback cleanup
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Log.info("Shutdown hook triggered. Performing cleanup...");
            if (driver != null) {
                driver.quit();
                Log.info("WebDriver closed via shutdown hook.");
            }
            if (conn != null) {
                dbOp.closeConnection(conn);
                Log.info("Database connection closed via shutdown hook.");
            }
        }));

        conn = dbOp.connectToDatabase();

        // Setting up ChromeDriver
        BrowserDriver bd = new BrowserDriver();
        driver = bd.getDriver();

        prop = new Properties();
        String baseUrl = prop.getSiteUrl();
        driver.get(baseUrl);

        ce = new CommonElements(driver);
        homePage = new HomePage(driver);

        // Reject cookies to prepare the page
        ce.clickRejectCookies();
    }

    @AfterTest
    public void tearDown() {
        driver.quit();
        dbOp.closeConnection(conn);
    }
}