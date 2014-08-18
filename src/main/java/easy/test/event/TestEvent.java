package easy.test.event;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import easy.test.exception.TestFailureException;
import easy.test.util.KeyUtils;

public class TestEvent {

    private EventType eventType;

    private String target;

    private List<DataRow> dataRows = new ArrayList<DataRow>();

    void action(WebDriver driver, int iteration) throws TestFailureException {

        int rowToProcess = iteration % dataRows.size();

        DataRow value = dataRows.get(rowToProcess);

        EventType eventTypeToUse = eventType;
        if (value.getOverrideEventType() != null) {
            eventTypeToUse = value.getOverrideEventType();
        }

        String targetToUse = target;
        if (StringUtils.isNotBlank(value.getOverrideTarget())) {
            targetToUse = value.getOverrideTarget();
        }

        System.out.println("eventType: " + eventTypeToUse + ". target: " + targetToUse + ". iteration: " + iteration
                + " data: " + dataRows.get(rowToProcess).getData());

        switch (eventTypeToUse) {
        case GO:
            driver.get(targetToUse);
            break;
        case WAIT:
            int waitPeriod = 1000;
            if (value != null) {
                waitPeriod = Integer.parseInt(targetToUse) * 1000;
            }
            try {
                Thread.sleep(waitPeriod);
            } catch (InterruptedException e) {
            }
            break;
        case TYPE:
            WebDriverWait wait = new WebDriverWait(driver, 5);
            if (StringUtils.isNotEmpty(value.getData())) {
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(targetToUse)));
                WebElement element = driver.findElement(By.cssSelector(targetToUse));
                element.click(); // this is to handle fields with a mask that need to be clicked first (see
                                 // stackoverflow)
                element.sendKeys(KeyUtils.getKeySequence(value.getData()));
            }
            break;
        case CLEAR:
            WebElement element = driver.findElement(By.cssSelector(targetToUse));
            element.clear();
            break;
        case CLICK:
            wait = new WebDriverWait(driver, 5);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(targetToUse)));
            element = driver.findElement(By.cssSelector(targetToUse));
            element.click();
            break;
        case CHECK:
            if (StringUtils.isBlank(value.getData())) {
                // check field is empty or not present
                try {
                    element = driver.findElement(By.cssSelector(targetToUse));
                } catch (Exception e) {
                    // expect no element to be found
                    break;
                }
                if (StringUtils.isNotBlank(element.getText())) {
                    throw new TestFailureException("Expected no value but got : " + element.getText());
                }
            } else {
                // check field is equal to specified value
                element = driver.findElement(By.cssSelector(targetToUse));

                List<WebElement> options = element.findElements(By.tagName("option"));
                if (CollectionUtils.isNotEmpty(options)) {
                    // select box
                    boolean matchFound = false;
                    for (WebElement option : options) {
                        if (StringUtils.equals(option.getAttribute("selected"), "true")
                                && StringUtils.equals(option.getText(), value.getData())) {
                            matchFound = true;
                            break;
                        }
                    }
                    if (!matchFound) {
                        throw new TestFailureException("Did not find expected value in select. Expected: "
                                + value.getData());
                    }
                } else if (!StringUtils.equals(element.getText(), value.getData())) {
                    throw new TestFailureException("Did not find expected value. Expected: " + value.getData()
                            + " and got: " + element.getText());
                }
            }
            break;
        case SELECT:

            wait = new WebDriverWait(driver, 60);
            wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(targetToUse)));

            element = driver.findElement(By.cssSelector(targetToUse));
            List<WebElement> options = element.findElements(By.tagName("option"));
            for (WebElement option : options) {
                if (option.getText().equals(value.getData())) {
                    option.click();
                    break;
                }
            }
            break;
        default:
            break;
        }

    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void add(String data) {
        DataRow dataRow = new DataRow();
        dataRow.setData(data);
        dataRows.add(dataRow);
    }

    public void add(String overrideTarget, String data) {
        DataRow dataRow = new DataRow();
        dataRow.setOverrideTarget(overrideTarget);
        dataRow.setData(data);
        dataRows.add(dataRow);
    }

    public void add(EventType overrideEventType, String overrideTarget, String data) {
        DataRow dataRow = new DataRow();
        dataRow.setOverrideEventType(overrideEventType);
        dataRow.setOverrideTarget(overrideTarget);
        dataRow.setData(data);
        dataRows.add(dataRow);
    }

    public class DataRow {

        private EventType overrideEventType;

        private String overrideTarget;

        private String data;

        public EventType getOverrideEventType() {
            return overrideEventType;
        }

        public void setOverrideEventType(EventType overrideEventType) {
            this.overrideEventType = overrideEventType;
        }

        public String getOverrideTarget() {
            return overrideTarget;
        }

        public void setOverrideTarget(String overrideTarget) {
            this.overrideTarget = overrideTarget;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

    }

}
