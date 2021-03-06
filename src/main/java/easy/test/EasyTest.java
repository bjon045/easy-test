package easy.test;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.safari.SafariDriver;

import easy.test.builder.Config;
import easy.test.builder.TestBuilder;
import easy.test.results.TestResults;
import easy.test.run.TestRun;
import easy.test.source.TestDataSource;
import easy.test.source.confluence.ConfluenceDataSource;

public class EasyTest {

    private TestDataSource testDataSource;

    public EasyTest(String testFilePath) {
        File workbookFile = new File(testFilePath);
        testDataSource = new ConfluenceDataSource();

        // testDataSource = new WorkbookTestDataSource(WorkbookUtils.load(workbookFile));
    }

    public void start(int start, int end) {
        // load configuration
        Config.loadConfig(testDataSource);

        // build test runs
        TestBuilder testBuilder = new TestBuilder();
        List<TestRun> testRuns = testBuilder.getTestRuns(testDataSource);
        TestResults testResults = new TestResults();

        for (String browser : Config.getBrowsers()) {
            WebDriver driver;
            if (StringUtils.equals(browser, "IE")) {
                System.setProperty("webdriver.ie.driver", "C:\\code\\easy-test\\IEDriverServer2.exe");
                // DesiredCapabilities caps = DesiredCapabilities.internetExplorer();
                // caps.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
                driver = new InternetExplorerDriver();
            } else if (StringUtils.equals(browser, "CHROME")) {
                driver = new ChromeDriver();
            } else if (StringUtils.equals(browser, "SAFARI")) {
                driver = new SafariDriver();
            } else {
                driver = new FirefoxDriver();
            }

            for (TestRun run : testRuns) {
                run.runTest(driver, testResults);
            }
            testResults.end();
            driver.close();
        }

    }

    public static void main(String[] args) {
        // syntax: java easy.test.EasyTest fileName (startNo) (endNo)
        // 2 3
        String testFilePath = "C:\\code\\easy-test\\test2.xlsx";
        int start = 0;
        int end = Integer.MAX_VALUE;
        if (args == null || args.length == 0) {
            System.out.println("Using defaults.");
        } else if (args.length == 1) {
            testFilePath = args[0];
        } else if (args.length == 2) {
            testFilePath = args[0];
            start = Integer.parseInt(args[1]);
        } else if (args.length == 3) {
            testFilePath = args[0];
            start = Integer.parseInt(args[1]);
            end = Integer.parseInt(args[2]);
        }

        EasyTest test = new EasyTest(testFilePath);
        test.start(start, end);
    }
}
