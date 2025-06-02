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

//        Get the page ready
        ce.clickRejectCookies();
        popBets.clickMore();

//        Step 1: Check if saved in DB events are no longer valid
        /*
            1.1 Get IDs from the DB for not-played events
            1.2 Search these events on the page
            1.3 Home odds outside of range?
                - yes: delete event from the DB
                - no: do nothing (the event will get inspected on its own turn)
         */

//        Step 2: Apply phase 1 filters to get events for further evaluation
        List<EventMetadata> phaseOneEvents = popBets.getPhaseOneEvents();

        if (phaseOneEvents.isEmpty()) {
            Log.info("Phase 1 evaluation: no events found");
        } else {
            for (EventMetadata event: phaseOneEvents) {

//                2.1: Print events to be evaluated further
                Log.info("Phase 1 evaluation returned " + phaseOneEvents.size() + " event(s): \n");
                Log.info("Name: " + event.getEventName());
                Log.info("Url: " + event.getHref());
                Log.info("ID: " + event.getId());
                Log.info("Home odds: " + event.getHomeOdds());
                Log.info("Home clicks: " + event.getHomeClicks());
                Log.info("Home clicks pct: " + event.getHomeClicksPct()  + "\n");
            }

            for (EventMetadata event: phaseOneEvents) {

//                Open event in a new tab
                SeleniumMethods sm = new SeleniumMethods(driver);
                sm.openNewTab(event.getHref());

                /*
                    2.2 Does event already exist in the DB?
                        - yes: update event's data
                        - no: perform phase 2 evaluation and add event if evaluation passes
                 */

//                Close tab
                sm.closeTab();

            }
        }
    }
}