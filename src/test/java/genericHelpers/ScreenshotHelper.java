package genericHelpers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for capturing screenshots during test execution.
 * Provides centralized screenshot handling with timestamped filenames
 * and organized storage in the logs/screenshots directory.
 */
public class ScreenshotHelper {

    private static final Logger Log = LogManager.getLogger(ScreenshotHelper.class.getName());
    private static final String SCREENSHOT_DIR = "logs/screenshots";

    /**
     * Captures a screenshot and saves it to the logs/screenshots directory
     * with a timestamped filename to prevent overwrites.
     *
     * @param driver the ChromeDriver instance
     * @param testName the name of the test for filename identification
     * @return the File object representing the saved screenshot, or null if capture failed
     */
    public static File captureScreenshot(ChromeDriver driver, String testName) {
        if (driver == null) {
            Log.warn("Cannot capture screenshot: ChromeDriver is null");
            return null;
        }

        try {
            // Ensure screenshot directory exists
            ensureScreenshotDirectoryExists();

            // Generate timestamped filename to prevent overwrites
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS").format(new Date());
            String filename = String.format("screenshot-%s-%s.png", testName, timestamp);
            File destination = new File(SCREENSHOT_DIR, filename);

            // Capture and save screenshot
            File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);

            Log.info("Screenshot captured successfully: " + destination.getAbsolutePath());
            return destination;

        } catch (Exception e) {
            Log.error("Failed to capture screenshot: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Ensures the screenshot directory exists, creating it if necessary.
     */
    private static void ensureScreenshotDirectoryExists() {
        File dir = new File(SCREENSHOT_DIR);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                Log.info("Created screenshot directory: " + dir.getAbsolutePath());
            } else {
                Log.warn("Failed to create screenshot directory: " + dir.getAbsolutePath());
            }
        }
    }
}
