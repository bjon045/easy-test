package easy.test.builder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import easy.test.source.TestDataSource;

public class Config {

    private static final Map<String, String> KEY_VALUE_PAIRS = new HashMap<String, String>();

    private static String[] browsers;

    private static URI jiraURI;

    private static String jiraUser;

    private static String jiraPass;

    public static void loadConfig(TestDataSource testDataSource) {
        String[][] configData = testDataSource.getDataSet("config");
        for (int i = 0; i < configData.length; i++) {
            KEY_VALUE_PAIRS.put(configData[i][0], configData[i][1]);
        }

        browsers = testDataSource.getDataSet("browsers")[0];

        String[][] jiraDataset = testDataSource.getDataSet("jira");
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
