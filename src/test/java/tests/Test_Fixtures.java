package tests;

import databaseHelpers.DatabaseOperations;
import databaseHelpers.EventOperations;
import genericHelpers.*;
import org.openqa.selenium.By;
import pageClasses.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.time.Duration;
import java.util.List;

public class Test_Fixtures  {

    private static final Logger Log = LogManager.getLogger(Test_Fixtures.class.getName());

    private final DatabaseOperations dbOp = new DatabaseOperations();
    private Connection conn = null;
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
            if (conn != null) {
                dbOp.closeConnection(conn);
                Log.info("Database connection closed via shutdown hook.");
            }
        }));

        conn = dbOp.connectToDatabase();

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
        dbOp.closeConnection(conn);
        
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
                - no: do nothing (the event will get inspected in following steps)
         */

//        Step 2: Apply phase 1 filters to get events for further evaluation
        List<EventMetadata> phaseOneEvents = popBets.getPhaseOneEvents();

        if (phaseOneEvents.isEmpty()) {
            Log.info("Phase 1 evaluation failed: no events found");
        } else {
            Log.info("Phase 1 evaluation returned " + phaseOneEvents.size() + " event(s): \n");
            for (EventMetadata event: phaseOneEvents) {

//                2.1: Print event to be evaluated further
                Log.info("Name: " + event.getEventName());
                Log.info("Url: " + event.getHref());
                Log.info("Home odds: " + event.getHomeOdds());
                Log.info("Home clicks: " + event.getHomeClicks());
                Log.info("Home clicks pct: " + event.getHomeClicksPct());

//                Open event in a new tab
                SeleniumMethods sm = new SeleniumMethods(driver);
                sm.openNewTab(event.getHref());
                sm.waitForElement(By.id("standingsComponent"), Duration.ofSeconds(5));
                EventDetails ed = new EventDetails(driver);

                /*
                    2.2 Does event already exist in the DB? --> todo
                        - yes: update event's data --> todo
                        - no: phase 2 evaluation passes? -> done
                            - yes: add event to db --> todo
                            - no: do nothing --> done
                 */

                Log.info("Starting phase 2 evaluation for " + event.getEventName() + "...");
                if (
                        ed.isStandingsOk()
                        && ed.isStandingsOk()
                        && ed.isLastH2hGameOk()
                ) {
                    Log.info("Phase 2 evaluation successful!");
                    //WIP: add event to DB
                    EventOperations eo = new EventOperations(conn, event, ed);
                    eo.addEvent();
                }

//                Close tab
                sm.closeTab();
            }
        }
    }
}