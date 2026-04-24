package genericHelpers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG listener for automatic screenshot capture on test failures.
 * This listener is registered in testng.xml and will automatically capture
 * a screenshot whenever a test fails, without requiring individual test modifications.
 */
public class TestFailureListener implements ITestListener {

    private static final Logger Log = LogManager.getLogger(TestFailureListener.class.getName());

    /**
     * Called when a test fails. Attempts to capture a screenshot.
     * Gracefully handles cases where the WebDriver is not available.
     *
     * @param result the ITestResult object containing test execution information
     */
    @Override
    public void onTestFailure(ITestResult result) {
        Log.error("Test failed: " + result.getName());

        // Log the actual exception/error details
        Throwable throwable = result.getThrowable();
        if (throwable != null) {
            Log.error("Exception: " + throwable.getMessage(), throwable);
        }

        try {
            // Try to retrieve the ChromeDriver from the test instance
            ChromeDriver driver = getDriverFromTestInstance(result.getInstance());

            if (driver != null) {
                Log.info("Attempting to capture screenshot for failed test: " + result.getName());
                ScreenshotHelper.captureScreenshot(driver, result.getName());
            } else {
                Log.warn("Cannot capture screenshot: ChromeDriver not available in test instance");
            }
        } catch (Exception e) {
            Log.error("Error during screenshot capture: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves the ChromeDriver instance from the test class if available.
     * Looks for a 'driver' field in the test instance, traversing the inheritance hierarchy.
     *
     * @param testInstance the test class instance
     * @return the ChromeDriver instance, or null if not found or inaccessible
     */
    private ChromeDriver getDriverFromTestInstance(Object testInstance) {
        if (testInstance == null) {
            return null;
        }

        try {
            // Traverse class hierarchy to find 'driver' field (handles inheritance from BaseTest)
            Class<?> clazz = testInstance.getClass();
            while (clazz != null && clazz != Object.class) {
                try {
                    java.lang.reflect.Field driverField = clazz.getDeclaredField("driver");
                    driverField.setAccessible(true);
                    Object driverObj = driverField.get(testInstance);
                    if (driverObj instanceof ChromeDriver) {
                        return (ChromeDriver) driverObj;
                    }
                } catch (NoSuchFieldException e) {
                    // Field not in this class, try superclass
                }
                clazz = clazz.getSuperclass();
            }
            return null;
        } catch (IllegalAccessException e) {
            Log.error("Failed to access driver field: " + e.getMessage());
            return null;
        }
    }
}
