package tests;

import helpers.BrowserDriver;
import helpers.EventMetadata;
import helpers.Properties;
import helpers.SeleniumMethods;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import pageClasses.CommonElements;
import pageClasses.EventDetails;
import pageClasses.PopularBets;

import java.time.Duration;
import java.util.List;

public class Test_Sandbox {

    private static final Logger Log = LogManager.getLogger(Test_Sandbox.class.getName());

//    private final DatabaseOperations dbOp = new DatabaseOperations();
//    private Connection conn = null;
    private ChromeDriver driver;

    @BeforeSuite
    public void setUp() {

        // Register the shutdown hook for fallback cleanup
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Log.info("Shutdown hook triggered. Performing cleanup...");
            if (driver != null) {
                driver.quit();
                Log.info("WebDriver closed via shutdown hook.");
            }
//            if (conn != null) {
//                dbOp.closeConnection(conn);
//                Log.info("Database connection closed via shutdown hook.");
//            }
        }));

//        conn = dbOp.connectToDatabase();

        // Setting up ChromeDriver
        BrowserDriver bd = new BrowserDriver();
        driver = bd.getDriver();

        Properties prop = new Properties();
        String sandboxUrl = prop.getSandboxUrl();
        driver.get(sandboxUrl);

    }

    @AfterSuite
    public void tearDown() {
        driver.quit();

//        Insert background job timestamp

//        BackgroundJobs bj = new BackgroundJobs(conn);
//        String jobName = Test_Fixtures.class.getSimpleName();
//        bj.addToBackgroundJobLog(jobName);

//        Close connection
//        dbOp.closeConnection(conn);
        
    }

    @Test
    public void testFixtures() {

//        Getting necessary classes
        CommonElements ce = new CommonElements(driver);
        EventDetails ed = new EventDetails(driver);

//        Get the page ready
        ce.clickRejectCookies();

//        Test UI actions
        Log.info("Sport: " + ed.getSport());
        Log.info("Country: " + ed.getCountry());
        Log.info("League: " + ed.getLeague());
        Log.info("Main Score: " + ed.getMainScore());
        Log.info("Is score null: " + (ed.getMainScore() == null));
        Log.info("Detailed Score: " + ed.getDetailedScore());
    }
}