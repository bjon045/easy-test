package easy.test.event;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;

import easy.test.exception.TestFailureException;

public class TestEventBlock {

    private List<TestEvent> events = new ArrayList<TestEvent>();

    private int iterations;

    public void runTest(WebDriver driver, int iteration) throws TestFailureException {
        for (TestEvent event : events) {
            event.action(driver, iteration);
        }
    }

    public void add(TestEvent event) {
        events.add(event);
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

}
