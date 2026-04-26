package tests;

import databaseHelpers.DatabaseOperations;
import genericHelpers.BrowserDriver;
import genericHelpers.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import pageClasses.CommonElements;
import pageClasses.HomePage;

import java.sql.Connection;

public class BaseTest {

    private static final Logger Log = LogManager.getLogger(BaseTest.class.getName());

    protected final DatabaseOperations dbOp = new DatabaseOperations();
    protected Connection conn = null;
    protected ChromeDriver driver;
    protected Properties prop;
    protected HomePage homePage;
    protected CommonElements ce;

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

        prop = new Properties();
        String baseUrl = prop.getSiteUrl();
        driver.get(baseUrl);

        // Perform network health check before proceeding
        performNetworkHealthCheck(baseUrl);

        ce = new CommonElements(driver);
        homePage = new HomePage(driver);

        // Reject cookies to prepare the page
        ce.clickRejectCookies();
    }

    @AfterTest
    public void tearDown() {
        driver.quit();
        dbOp.closeConnection(conn);
    }

    /**
     * Performs a network health check to ensure adequate connectivity before test execution.
     * Skips the test if network conditions are degraded.
     *
     * @param baseUrl the base URL to test connectivity against
     * @throws SkipException if network health check fails
     */
    protected void performNetworkHealthCheck(String baseUrl) {
        Log.info("Performing network health check...");

        try {
            long startTime = System.currentTimeMillis();
            
            // Navigate to a lightweight endpoint or reload current page to measure response time
            driver.navigate().refresh();
            
            long loadTime = System.currentTimeMillis() - startTime;
            Log.info("Page load time: " + loadTime + "ms");

            // Define thresholds for network quality
            final long SLOW_NETWORK_THRESHOLD = 5000; // 5 seconds
            final long CRITICAL_THRESHOLD = 10000;      // 10 seconds

            if (loadTime > CRITICAL_THRESHOLD) {
                Log.error("CRITICAL: Network response time exceeded " + CRITICAL_THRESHOLD + "ms. "
                        + "Current load time: " + loadTime + "ms");
                throw new RuntimeException("Network conditions critically degraded. "
                        + "Test skipped to avoid timeout failures.");
            } else if (loadTime > SLOW_NETWORK_THRESHOLD) {
                Log.warn("WARNING: Slow network detected. Page load time: " + loadTime + "ms. "
                        + "Test may be prone to timeouts. Consider adjusting element wait timeouts.");
            } else {
                Log.info("Network health check passed. Load time within acceptable range.");
            }

        } catch (Exception e) {
            Log.error("Network health check failed with exception: " + e.getMessage());
            throw new RuntimeException("Network health check failed. Unable to verify connectivity. "
                    + "Error: " + e.getMessage());
        }
    }
}