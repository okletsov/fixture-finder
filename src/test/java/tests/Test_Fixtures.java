package tests;

import helpers.*;
import pageClasses.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.util.List;

public class Test_Fixtures  {

    private static final Logger Log = LogManager.getLogger(Test_Fixtures.class.getName());

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
        driver.manage().window().maximize();

        Properties prop = new Properties();
        String baseUrl = prop.getSiteUrl();
        driver.get(baseUrl);

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
        PopularBets popBets = new PopularBets(driver);

//        Performing UI actions
        ce.clickRejectCookies();
        popBets.clickMore();

//        Apply phase 1 filters to get events for further evaluation
        List<EventMetadata> phaseOneEvents = popBets.getPhaseOneEvents();

        if (phaseOneEvents.isEmpty()) {
            Log.info("Phase 1 evaluation: no events found");
        } else {
            for (EventMetadata event: phaseOneEvents) {
                Log.info("Phase 1 evaluation returned " + phaseOneEvents.size() + " event(s): \n");
                Log.info("Name: " + event.getEventName());
                Log.info("Home odds: " + event.getHomeOdds());
                Log.info("Home clicks: " + event.getHomeClicks());
                Log.info("Home clicks pct: " + event.getHomeClicksPct()  + "\n");
            }
        }
    }
}