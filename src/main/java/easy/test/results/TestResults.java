package easy.test.results;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

/**
 * TODO: replace this poorly implemented class with a decent template based outputter
 */
public class TestResults {

    private StringBuilder resultHTML = new StringBuilder();

    public TestResults() {
        resultHTML
                .append("<HTML><body><table><tr><th>TEST RUN</th><th>ITERATION</th><th>RESULT</th><th>SCREENSHOT</th></tr>");
    }

    public void end() {
        resultHTML.append("</table></body></HTML>");

        File file = new File("results/results.html");
        try {
            FileUtils.writeStringToFile(file, resultHTML.toString());
        } catch (IOException e) {
            throw new RuntimeException("Error creating results file.", e);
        }
    }

    public void recordResult(File newFile, boolean success, int runNumber, int iteration) {
        resultHTML.append("<tr>");
        resultHTML.append("<td>").append(runNumber).append("</td>");
        resultHTML.append("<td>").append(iteration).append("</td>");
        resultHTML.append("<td>").append(success ? "Sucess" : "Failure").append("</td>");
        if (newFile == null) {
            resultHTML.append("<td>Screen shot failed.</td>");
        } else {
            resultHTML.append("<td><a href=\"../").append(newFile.getPath()).append("\">VIEW</a></td>");
        }
        resultHTML.append("</tr>");
    }

}
