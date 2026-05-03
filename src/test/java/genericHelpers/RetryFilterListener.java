package genericHelpers;

import org.testng.IAlterSuiteListener;
import org.testng.xml.XmlSuite;

import java.util.Arrays;
import java.util.List;

public class RetryFilterListener implements IAlterSuiteListener {

    @Override
    public void alter(List<XmlSuite> suites) {
        String retryTests = System.getProperty("retryTests");
        if (retryTests == null) return;

        List<String> testsToRun = Arrays.asList(retryTests.split(","));

        for (XmlSuite suite : suites) {
            suite.getTests().removeIf(t -> !testsToRun.contains(t.getName()));
        }
    }
}