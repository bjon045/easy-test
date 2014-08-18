package easy.test.builder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;

import easy.test.util.WorkbookUtils;

public class Config {

    private static final Map<String, String> KEY_VALUE_PAIRS = new HashMap<String, String>();

    private static String[] browsers;

    private static URI jiraURI;

    private static String jiraUser;

    private static String jiraPass;

    public static void loadConfig(Workbook workbook) {
        String[][] configData = WorkbookUtils.getDataSet("config", workbook);
        for (int i = 0; i < configData.length; i++) {
            KEY_VALUE_PAIRS.put(configData[i][0], configData[i][1]);
        }

        browsers = WorkbookUtils.getDataSet("browsers", workbook)[0];

        String[][] jiraDataset = WorkbookUtils.getDataSet("jira", workbook);
        String jiraUrl = jiraDataset[0][0];
        if (StringUtils.isNotBlank(jiraUrl)) {
            try {
                jiraURI = new URI(jiraUrl);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }

            jiraUser = jiraDataset[0][1];
            jiraPass = jiraDataset[0][2];
        }
    }

    public static String getValue(String key) {
        String value = KEY_VALUE_PAIRS.get(key);
        if (value == null) {
            return key;
        }
        return value;
    }

    public static String[] getBrowsers() {
        return browsers;
    }

    public static URI getJiraURI() {
        return jiraURI;
    }

    public static String getJiraUser() {
        return jiraUser;
    }

    public static String getJiraPass() {
        return jiraPass;
    }

}
