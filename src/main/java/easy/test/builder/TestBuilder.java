package easy.test.builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;

import easy.test.event.EventType;
import easy.test.event.TestEvent;
import easy.test.event.TestEventBlock;
import easy.test.run.TestRun;
import easy.test.util.WorkbookUtils;

public class TestBuilder {

    public List<TestRun> getTestRuns(Workbook workbook) {
        List<TestRun> testRuns = new ArrayList<TestRun>();

        String[][] testRunsData = WorkbookUtils.getDataSet("testRuns", workbook);
        for (int i = 0; i < testRunsData.length; i++) {
            TestRun testRun = new TestRun();
            testRun.setRunNumber(i + 1);
            testRuns.add(testRun);

            String[] runData = testRunsData[i];

            for (int x = 0; x < runData.length; x++) {
                String rawRunName = runData[x]; // name yet to looked up in config
                if (StringUtils.isBlank(rawRunName)) {
                    continue;
                }

                if (rawRunName.startsWith("jira:")) {
                    testRun.setJiraNumber(rawRunName.substring(5));
                    continue;
                }

                TestEventBlock testEventBlock = new TestEventBlock();
                testRun.add(testEventBlock);

                String testRunName = Config.getValue(rawRunName); // run name after config lookup
                String[][] eventBlock = WorkbookUtils.getDataSet(testRunName, workbook);
                testEventBlock.setIterations(eventBlock.length - 2);

                for (int y = 0; y < eventBlock[0].length; y++) {
                    // columns
                    if (StringUtils.isBlank(eventBlock[0][y])) {
                        continue;
                    }
                    TestEvent event = new TestEvent();
                    testEventBlock.add(event);
                    String eventType = eventBlock[0][y];
                    if (StringUtils.isNotBlank(eventType)) {
                        event.setEventType(EventType.valueOf(eventType));
                    }
                    event.setTarget(Config.getValue(eventBlock[1][y]));

                    for (int z = 2; z < eventBlock.length; z++) {
                        // row
                        String dataValue = Config.getValue(eventBlock[z][y]);
                        String[] dataValues = StringUtils.split(dataValue, "|");
                        if (dataValues == null || dataValues.length == 0) {
                            event.add(null);
                        } else if (dataValues.length == 1) {
                            if (StringUtils.isBlank(event.getTarget())) {
                                event.add(dataValues[0], null);
                            } else {
                                event.add(dataValues[0]);
                            }

                        } else if (dataValues.length == 2) {
                            event.add(dataValues[0], dataValues[1]);
                        } else if (dataValues.length == 3) {
                            event.add(EventType.valueOf(dataValues[0]), dataValues[1], dataValues[2]);
                        } else {
                            throw new RuntimeException("Badly formed data parsing row:" + z + " col: " + y
                                    + " and testName: " + testRunName);
                        }

                    }

                }

            }

        }

        return testRuns;
    }
}
