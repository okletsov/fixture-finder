package tests;

import databaseHelpers.DatabaseOperations;
import databaseHelpers.EventOperations;
import genericHelpers.*;
import org.openqa.selenium.By;
import org.testng.annotations.*;
import pageClasses.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.chrome.ChromeDriver;

import java.sql.Connection;
import java.time.Duration;
import java.util.List;

public class Test_AddFixtures {

    private static final Logger Log = LogManager.getLogger(Test_AddFixtures.class.getName());

    private final DatabaseOperations dbOp = new DatabaseOperations();
    private Connection conn = null;
    private ChromeDriver driver;

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

        Properties prop = new Properties();
        String baseUrl = prop.getSiteUrl();
        driver.get(baseUrl);

    }

    @AfterTest
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
                Log.info("Odds: " + event.getHomeOdds() + "; Clicks: " + event.getHomeClicks() + "; Pct: " + event.getHomeClicksPct());

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