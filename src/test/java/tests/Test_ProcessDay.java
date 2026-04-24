package tests;

import databaseHelpers.EventOperations;
import databaseHelpers.SqlLoader;
import genericHelpers.EventMetadata;
import genericHelpers.SeleniumMethods;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import pageClasses.EventDetails;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Test_ProcessDay extends BaseTest {

    private static final Logger Log = LogManager.getLogger(Test_ProcessDay.class.getName());

    @Test
    @Parameters("day")
    public void processDay(String day) {
        // Load events once for the day
        if (day.equals("tomorrow")) {
            homePage.clickNextDayBtn();
            Log.info("Navigated to tomorrow");
        }
        homePage.loadAllEvents();

        // Phase 1: Check existing fixtures and validate home odds
        Log.info("========== CHECKING EXISTING FIXTURES ==========");
        checkExistingFixtures();

        // Phase 2: Add new fixtures
        Log.info("========== ADDING NEW FIXTURES ==========");
        addNewFixtures();
    }

    private void checkExistingFixtures() {
        /*
            Check if saved in DB events are no longer valid:
                1 Get IDs from the DB for not-played events
                2 Search these events on the page
                3 Home odds outside of range?
                  - yes: delete event from the DB
                  - no: do nothing (the event will get inspected in following steps)
         */

        SqlLoader sqlLoader = new SqlLoader("sql/get_future_events.sql");
        String sql = sqlLoader.getSql();
        ArrayList<String> futureEventIds = dbOp.getArray(conn, "id", sql);

        Log.info("Evaluating home odds for " + futureEventIds.size() + " future events");
        for (String id : futureEventIds) {
            BigDecimal homeOdds = homePage.getHomeOddsById(id);
            BigDecimal homeOddsMin = prop.getHomeOddsMin();
            BigDecimal homeOddsMax = prop.getHomeOddsMax();
            if (
                    homeOdds != null
                    && (homeOdds.compareTo(homeOddsMin) < 0
                    || homeOdds.compareTo(homeOddsMax) > 0)
            ) {
                Log.info("Home odds outside of range, deleting " + id + " event...");
                EventOperations eo = new EventOperations(conn);
                eo.deleteEventById(id);
            }
        }
        Log.info("All future events evaluated\n");
    }

    private void addNewFixtures() {
        //            Apply phase 1 filters to get events for further evaluation
        List<EventMetadata> phaseOneEvents = homePage.getPhaseOneEvents();

        // Reclaim memory: unload homePage DOM and release cached WebElements
        driver.navigate().to("about:blank");
        homePage = null;

        if (phaseOneEvents.isEmpty()) {
            Log.info("Phase 1 evaluation failed: no events found");
        } else {
            Log.info("Phase 1 evaluation returned " + phaseOneEvents.size() + " event(s): \n");
            for (EventMetadata eventMetadata: phaseOneEvents) {

//                Print event to be evaluated further
                Log.info("Name: " + eventMetadata.getEventName());
                Log.info("Url: " + eventMetadata.getHref());
                Log.info("Odds: " + eventMetadata.getHomeOdds());

//                Open event in a new tab
                SeleniumMethods sm = new SeleniumMethods(driver);
                sm.openNewTab(eventMetadata.getHref());

//                    Waiting for the standings table and the list of odds to load
                sm.waitForElement(By.id("standingsComponent"), Duration.ofSeconds(20));
                sm.waitForElementListToLoad(By.cssSelector("#best-odds-0 > tr"), Duration.ofSeconds(20));

                EventDetails eventDetails = new EventDetails(driver);

                /*
                    Does event already exist in the DB?
                        - yes: update event's data
                        - no: phase 2 evaluation passes?
                            - yes: add event to db
                            - no: do nothing
                 */

                EventOperations eo = new EventOperations(conn, eventMetadata, eventDetails);
                boolean eventExistsInDb = eo.getEventById(eventMetadata.getId()) != null;

                if (eventExistsInDb) {
                    eo.updateEvent();
                } else if (
                        eventDetails.isTournamentOk()
                        && eventDetails.isStandingsOk()
                        && eventDetails.isLastH2hGameOk()
                ) {
                    Log.info("Phase 2 evaluation successful!\n");
                    eo.addEvent();
                }

//                Close tab
                sm.closeTab();
            }
        }
    }
}
