package easy.test.source.confluence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.io.IOUtils;

import com.google.gson.Gson;

import easy.test.source.TestDataSource;

@SuppressWarnings("deprecation")
public class ConfluenceDataSource implements TestDataSource {

    private Map<String, String[][]> datasets = new HashMap<String, String[][]>();

    public ConfluenceDataSource() {
        String result = getPageResponse("http://confluence.int.corp.sun/rest/prototype/latest/content/133105228.json");

        Gson gson = new Gson();
        ConfluencePage page = gson.fromJson(result, ConfluencePage.class);

        Document document = Jsoup.parse(page.getBody().getValue());
        Elements tables = document.getElementsByTag("table");
        for (Element table : tables) {
            Elements rows = table.select("tr");
            Elements colHeader = rows.first().select("td");
            String tableName = colHeader.first().text();

            String[][] data = new String[rows.size() - 1][colHeader.size()];

            for (int i = 1; i < rows.size(); i++) {
                // ignore first and last row
                Element row = rows.get(i);
                Elements cells = row.select("td");
                for (int z = 0; z < cells.size(); z++) {
                    Element cell = cells.get(z);
                    String cellValue = StringUtils.strip(cell.text(), "\u00a0");

                    if (StringUtils.isNotBlank(cellValue)) {
                        data[i - 1][z] = cellValue;
                    }
                    // System.out.println(cell.text());
                }
            }
            System.out.println(tableName);
            datasets.put(tableName, data);
        }

    }

    @Override
    public String[][] getDataSet(String name) {
        return datasets.get(name);
    }

    private String getPageResponse(String page) {
        StringBuilder responseString = new StringBuilder();

        HttpHost targetHost = new HttpHost("confluence.int.corp.sun", 80, "http");

        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("u366423", "bruce777"));

        AuthCache authCache = new BasicAuthCache();
        authCache.put(targetHost, new BasicScheme());

        final HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(credsProvider);
        context.setAuthCache(authCache);

        HttpClient httpClient = HttpClientBuilder.create().build();

        HttpGet httpGet = new HttpGet(page);
        HttpResponse response;
        try {
            response = httpClient.execute(httpGet, context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Get hold of the response entity
        HttpEntity entity = response.getEntity();

        // If the response does not enclose an entity, there is no need
        // to worry about connection release
        if (entity != null) {
            InputStream instream;
            try {
                instream = entity.getContent();
            } catch (IllegalStateException | IOException e) {
                throw new RuntimeException(e);
            }
            try {

                BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
                // do something useful with the response
                responseString.append(reader.readLine());

            } catch (IOException e) {
                throw new RuntimeException(e);

            } catch (RuntimeException e) {

                // In case of an unexpected exception you may want to abort
                // the HTTP request in order to shut down the underlying
                // connection and release it back to the connection manager.
                httpGet.abort();
                throw e;

            } finally {

                // Closing the input stream will trigger connection release
                IOUtils.closeQuietly(instream);

            }

            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
        }
        httpClient.getConnectionManager().shutdown();
        return responseString.toString();
    }

    public class ConfluencePage {

        private ConfluenceBody body;

        public ConfluenceBody getBody() {
            return body;
        }

        public void setBody(ConfluenceBody body) {
            this.body = body;
        }

    }

    public class ConfluenceBody {

        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

}
