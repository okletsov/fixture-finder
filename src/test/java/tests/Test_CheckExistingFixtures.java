package tests;

import databaseHelpers.DatabaseOperations;
import databaseHelpers.EventOperations;
import databaseHelpers.SqlLoader;
import genericHelpers.BrowserDriver;
import genericHelpers.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.*;
import pageClasses.CommonElements;
import pageClasses.PopularBets;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;

public class Test_CheckExistingFixtures {

    private static final Logger Log = LogManager.getLogger(Test_CheckExistingFixtures.class.getName());

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
    public void testExistingFixtures() {

//        Getting necessary classes
        CommonElements ce = new CommonElements(driver);
        PopularBets popBets = new PopularBets(driver);
        Properties prop = new Properties();

//        Get the page ready
        ce.clickRejectCookies();
        popBets.clickMore();

        /*
            Check if saved in DB events are no longer valid:
                1 Get IDs from the DB for not-played events
                2 Search these events on the page
                3 Home odds outside of range?
                  - yes: delete event from the DB
                  - no: do nothing (the event will get inspected in following steps)
         */

        DatabaseOperations dbOp = new DatabaseOperations();
        SqlLoader sqlLoader = new SqlLoader("sql/get_future_events.sql");
        String sql = sqlLoader.getSql();
        ArrayList<String> futureEventIds = dbOp.getArray(conn, "id", sql);

        Log.info("Evaluating home odds for " + futureEventIds.size() + " future events");
        for (String id: futureEventIds) {
            BigDecimal homeOdds = popBets.getHomeOddsById(id);
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
}