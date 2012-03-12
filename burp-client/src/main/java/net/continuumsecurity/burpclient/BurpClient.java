/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.continuumsecurity.burpclient;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import net.continuumsecurity.restyburp.model.Config;
import net.continuumsecurity.restyburp.model.HttpRequestResponseBean;
import net.continuumsecurity.restyburp.model.ProxyHistoryList;
import net.continuumsecurity.restyburp.model.ScanIssueList;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author stephen
 */
public class BurpClient {
    static Logger log = Logger.getLogger(BurpClient.class.toString());
    Client client;
    WebResource service;

    public BurpClient(String baseUrl) {
        log.debug("Creating Jersey client to: "+baseUrl);
        client = createClient(null,0);
        service = client.resource(UriBuilder.fromUri(baseUrl).build());
    }
    
    public BurpClient(String baseUrl, String proxyHost, int proxyPort) {
        log.debug("Creating Jersey client to: "+baseUrl);

        client = createClient(proxyHost, proxyPort);
        service = client.resource(UriBuilder.fromUri(baseUrl).build());
    }

    public int scan(String url) throws Exception {
        String query = "target=" + URLEncoder.encode(url, "UTF-8");
        ClientResponse response = service.path("scanner").path("scan").post(ClientResponse.class, query);
        return response.getEntity(JSONObject.class).getInt("id");
    }

    public int percentComplete(int id) throws Exception {
        ClientResponse response = service.path("scanner").path(Integer.toString(id)).path("complete").get(ClientResponse.class);
        return response.getEntity(JSONObject.class).getInt("complete");
    }

    public ScanIssueList getIssueList(int scanId) {
        return (service.path("scanner").path(Integer.toString(scanId)).path("issues").get(ScanIssueList.class));
    }

    public List<HttpRequestResponseBean> getProxyHistory() {
        ProxyHistoryList result = service.path("proxy").path("history").get(ProxyHistoryList.class);
        return result.history;
    }

    public List<HttpRequestResponseBean> getProxyHistory(String url) throws UnsupportedEncodingException {
        ProxyHistoryList result = service.path("proxy").path("history").queryParam("url", url).get(ProxyHistoryList.class);
        return result.history;
    }
    
    /*
     * Matches using the following regex options:
     * Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE)
     */
    public HttpRequestResponseBean findInRequestHistory(String regex) {
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
        for (HttpRequestResponseBean bean : getProxyHistory()) {
            if (p.matcher(new String(bean.getRequest())).matches()) return bean;
        }
        return null;
    }
    
    public HttpRequestResponseBean findInResponseHistory(String regex) {
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
        for (HttpRequestResponseBean bean : getProxyHistory()) {
            if (p.matcher(new String(bean.getResponse())).matches()) return bean;
        }
        return null;
    }

    public Map<String, String> getConfig() {
        Config config = service.path("config").get(Config.class);
        log.debug(config.entries.toString());
        return config.entries;
    }

    public void setConfig(Map<String, String> entries) throws RuntimeException {
        service.path("config").type(MediaType.APPLICATION_JSON).put(Config.class, new Config(entries));
    }

    public void updateConfig(Map<String, String> entries) throws RuntimeException {
        service.path("config").type(MediaType.APPLICATION_JSON).post(Config.class, new Config(entries));
    }
    
    public void reset() throws RuntimeException {
        service.path("reset").type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
    }
    
    public void destroy() {
        client.destroy();
    }

    private Client createClient(String proxyHost, int proxyPort) {
        final DefaultApacheHttpClientConfig config = new DefaultApacheHttpClientConfig();
        if (!"".equals(proxyHost) && (proxyPort != 0)) {
            log.debug(" using proxy: "+proxyHost+":"+proxyPort);
            config.getProperties().put(DefaultApacheHttpClientConfig.PROPERTY_PROXY_URI, "http://" + proxyHost + ":" + proxyPort);
        } 
        return ApacheHttpClient.create(config);
    }
}
