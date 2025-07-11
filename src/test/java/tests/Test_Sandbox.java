package tests;

import databaseHelpers.DatabaseOperations;
import databaseHelpers.EventOperations;
import databaseHelpers.SqlLoader;
import genericHelpers.BrowserDriver;
import genericHelpers.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import pageClasses.CommonElements;
import pageClasses.EventDetails;

import java.sql.Connection;
import java.util.List;

public class Test_Sandbox {

    private static final Logger Log = LogManager.getLogger(Test_Sandbox.class.getName());

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

//        Get the page ready
        CommonElements ce = new CommonElements(driver);
        ce.clickRejectCookies();

//        Getting necessary classes
//        EventDetails ed = new EventDetails(driver);

//        Test UI actions
//        Log.info("Tournament passed evaluation: " + ed.isTournamentOk());
//        Log.info("Last h2h game passed evaluation: " + ed.isLastH2hGameOk());
//        Log.info("Standings evaluation: " + ed.isStandingsOk());
//        Log.info("Sport: " + ed.getSport());
//        Log.info("Country: " + ed.getCountry());
//        Log.info("League: " + ed.getLeague());
//        Log.info("Main Score: " + ed.getMainScore());
//        Log.info("Is score null: " + (ed.getMainScore() == null));
//        Log.info("Detailed Score: " + ed.getDetailedScore());
//        String href = "/football/georgia/crystalbet-erovnuli-liga-2/locomotive-tbilisi-bolnisi/tMAsIdAM/";
//        Log.info("Result: " + ed.getResult(href));
//        Log.info("Dropping odds: " + ed.getDroppingOddsCount());
//        Log.info("Dropping odds pct: " + ed.getDroppingOddsPct());
//        Log.info("Home team name: " + ed.getHomeTeamName());
//        Log.info("Last away h2h game date: " + ed.getEventDateByEvent(ed.getPlayedAwayH2hEvents().get(0)));
//        Log.info("Away odds for last h2h game: " + ed.getAwayOdds(ed.getOddsByEvent(ed.getPlayedAwayH2hEvents().get(0))));
//        Log.info("Home league pos: " + ed.getHomeLeaguePos());
//        Log.info("Away league pos: " + ed.getAwayLeaguePos());
//        Log.info("League teams count: " + ed.getLeagueTeamsCount());
//        Log.info("Home league pos pct: " + ed.getHomeLeaguePosPct());
//        Log.info("Away league pos pct: " + ed.getAwayLeaguePosPct());
//        Log.info("Home form: " + ed.getTeamForm(ed.getHomeTeamId()));
//        Log.info("Away form: " + ed.getTeamForm(ed.getAwayTeamId()));
//        Log.info("DateTime scheduled: " + ed.getDateTimeScheduled());
//        Log.info("League games played: " + ed.getLeagueGamesPlayed());

        EventOperations eo = new EventOperations(conn);
        boolean eventExistsInDb = eo.getEventById("jqhEH9V11") != null;
        System.out.println(eventExistsInDb);
    }
}