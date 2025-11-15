package tests;

import databaseHelpers.DatabaseOperations;
import databaseHelpers.EventOperations;
import databaseHelpers.SqlLoader;
import genericHelpers.BrowserDriver;
import genericHelpers.Properties;
import genericHelpers.SeleniumMethods;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import pageClasses.CommonElements;
import pageClasses.EventDetails;
import pageClasses.PopularBets;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.Duration;
import java.util.ArrayList;

public class Test_UpdateResults {

    private static final Logger Log = LogManager.getLogger(Test_UpdateResults.class.getName());

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
    public void testUpdateResults() {

//        Getting necessary classes
        CommonElements ce = new CommonElements(driver);

//        Get the page ready
        ce.clickRejectCookies();

        /*
        Step 3: updating result for finished events
            3.1 Get event ids from the DB
            3.2.Get URL
            3.3 Check if event is finished
            3.4 Get event result, main and detailed score
            3.5 Update DB record
         */

        DatabaseOperations dbOp = new DatabaseOperations();
        SqlLoader sqlLoader = new SqlLoader("sql/get_no_result_events.sql");
        String sql = sqlLoader.getSql();
        ArrayList<String> noResultEventIds = dbOp.getArray(conn, "id", sql);

        Log.info("Updating result for " + noResultEventIds.size() + " events");
        for (String id: noResultEventIds) {
            SeleniumMethods sm = new SeleniumMethods(driver);

//            Get event url from the db and open it in a new tab
            EventOperations eo = new EventOperations(conn);
            String url = eo.getUrlById(id);
            sm.openNewTab(url);
            sm.waitForElement(By.id("js-mutual-table"), Duration.ofSeconds(15));

//            Check if event was finished
            EventDetails ed = new EventDetails(driver);
            boolean isFinished = ed.isEventFinished();
            boolean isScorePresent = sm.isElementPresent("id", "js-score");

            if (isFinished && isScorePresent) {
//                Update main, detailed scores and result
                String mainScore = ed.getMainScore();
                String detailedScore = ed.getDetailedScore();
                String href = url.replace("https://www.betexplorer.com", "");
                String result = ed.getResult(href);

                eo.updateResultById(id, mainScore, detailedScore, result);
            }

//            Close tab
            sm.closeTab();
        }
        Log.info("Finished updating results\n");
    }
}