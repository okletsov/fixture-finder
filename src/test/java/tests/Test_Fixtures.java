package tests;

import databaseHelpers.DatabaseOperations;
import databaseHelpers.EventOperations;
import databaseHelpers.SqlLoader;
import genericHelpers.*;
import org.openqa.selenium.By;
import pageClasses.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.Duration;
import java.util.ArrayList;
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
        Properties prop = new Properties();

//        Get the page ready
        ce.clickRejectCookies();
        popBets.clickMore();

//        Step 1: Check if saved in DB events are no longer valid
        /*
            1.1 Get IDs from the DB for not-played events --> done
            1.2 Search these events on the page --> done
            1.3 Home odds outside of range? --> done
                - yes: delete event from the DB --> todo
                - no: do nothing (the event will get inspected in following steps) --> done
         */

//        1.1 Get IDs from the DB for not-played events
        DatabaseOperations dbOp = new DatabaseOperations();
        SqlLoader sqlLoader = new SqlLoader("sql/get_not_finished_events.sql");
        String sql = sqlLoader.getSql();
        ArrayList<String> notFinishedEventIds = dbOp.getArray(conn, "id", sql);

        Log.info("Evaluating home odds for " + notFinishedEventIds.size() + " not finished events");
        for (String id: notFinishedEventIds) {
            BigDecimal homeOdds = popBets.getHomeOddsById(id);
            BigDecimal homeOddsMin = prop.getHomeOddsMin();
            BigDecimal homeOddsMax = prop.getHomeOddsMax();
            if (
                    homeOdds != null
                    && (homeOdds.compareTo(homeOddsMin) < 0
                    || homeOdds.compareTo(homeOddsMax) > 0)
            ) {
                Log.info("Home odds outside of range, deleting " + id + " event...");
            }
            Log.info("Successfully evaluated not finished events\n");
        }

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
                Log.info("Odds: " + event.getHomeOdds() + "; Clicks" + event.getHomeClicks() + "; Pct: " + event.getHomeClicksPct());

//                Open event in a new tab
                SeleniumMethods sm = new SeleniumMethods(driver);
                sm.openNewTab(event.getHref());
                sm.waitForElement(By.id("standingsComponent"), Duration.ofSeconds(15));
                EventDetails ed = new EventDetails(driver);

                /*
                    2.2 Does event already exist in the DB?
                        - yes: update event's data
                        - no: phase 2 evaluation passes?
                            - yes: add event to db
                            - no: do nothing
                 */

                EventOperations eo = new EventOperations(conn, event, ed);
                boolean eventExistsInDb = eo.getEventById(event.getId()) != null;

                if (eventExistsInDb) {
                    eo.updateEvent();
                } else if (
                        ed.isTournamentOk()
                        && ed.isStandingsOk()
                        && ed.isLastH2hGameOk()
                ) {
                    Log.info("Phase 2 evaluation successful!");
                    eo.addEvent();
                }

//                Close tab
                sm.closeTab();
            }
        }
    }
}