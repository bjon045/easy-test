package easy.test.run;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.atlassian.jira.rest.client.IssueRestClient;
import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.domain.Comment;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

import easy.test.builder.Config;
import easy.test.event.TestEventBlock;
import easy.test.exception.TestFailureException;
import easy.test.results.TestResults;

public class TestRun {

    private List<TestEventBlock> eventBlocks = new ArrayList<TestEventBlock>();

    private int runNumber;

    private String jiraNumber;

    public void runTest(WebDriver driver, TestResults testResults) {
        int maxIterations = 0;
        for (TestEventBlock eventBlock : eventBlocks) {
            int blockMax = eventBlock.getIterations();
            if (maxIterations < blockMax) {
                maxIterations = blockMax;
            }
        }

        for (int i = 0; i < maxIterations; i++) {
            try {
                for (TestEventBlock eventBlock : eventBlocks) {
                    eventBlock.runTest(driver, i);
                }
                recordResult(testResults, driver, true, i);
            } catch (TestFailureException e) {
                recordResult(testResults, driver, false, i);
                e.printStackTrace();
            }
        }
    }

    public void recordResult(TestResults testResults, WebDriver driver, boolean success, int iteration) {
        File newFile = null;
        try {
            File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            // Now you can do whatever you need to do with it, for example copy somewhere
            newFile = new File("results/" + (success ? "SUCCESS" : "FAILURE") + "/test run " + runNumber + "-"
                    + iteration + " " + (success ? "SUCCESS" : "FAILURE") + ".png");
            try {
                FileUtils.copyFile(scrFile, newFile);
            } catch (IOException e) {
                throw new RuntimeException("Error creating screenshot", e);
            }
        } catch (Exception e) {
            // error recording result
        }

        testResults.recordResult(newFile, success, runNumber, iteration);
        if (StringUtils.isNotBlank(jiraNumber) && Config.getJiraURI() != null) {
            // create jira issue
            AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
            JiraRestClient restClient;
            restClient = factory.createWithBasicHttpAuthentication(Config.getJiraURI(), Config.getJiraUser(),
                    Config.getJiraPass());

            // try {
            IssueRestClient client = restClient.getIssueClient();

            Issue issue = client.getIssue(jiraNumber).claim();
            client.addComment(issue.getCommentsUri(),
                    Comment.valueOf("EastTest result: " + (success ? "Pass" : "Fail")));

            // Iterable<Transition> transitions = client.getTransitions(issue).get();
            // Transition transition = Iterables.find(transitions, new Predicate<Transition>() {
            //
            // @Override
            // public boolean apply(Transition arg0) {
            // System.out.println(arg0.getName());
            // return StringUtils.equals(arg0.getName(), "");
            // }
            //
            // });
            //
            // client.transition(issue, new TransitionInput(transition.getId()));
            // } catch (ExecutionException | InterruptedException e) {
            // throw new RuntimeException(e);
            // }
        }

    }

    public void add(TestEventBlock testEventBlock) {
        eventBlocks.add(testEventBlock);
    }

    public void setRunNumber(int runNumber) {
        this.runNumber = runNumber;
    }

    public void setJiraNumber(String jiraNumber) {
        this.jiraNumber = jiraNumber;
    }
}
