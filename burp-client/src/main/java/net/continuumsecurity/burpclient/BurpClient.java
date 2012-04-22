/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.continuumsecurity.burpclient;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
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
import net.continuumsecurity.restyburp.model.HttpMessage;
import net.continuumsecurity.restyburp.model.HttpMessageList;
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
    	PropertyConfigurator.configure("log4j.properties");
        log.debug("Creating Jersey client to: "+baseUrl);
        client = createClient(null,0);
        service = client.resource(UriBuilder.fromUri(baseUrl).build());
    }
    
    public BurpClient(String baseUrl, String proxyHost, int proxyPort) {
    	PropertyConfigurator.configure("log4j.properties");
        log.debug("Creating Jersey client to: "+baseUrl+" with proxy host: "+proxyHost+" and port: "+proxyPort);
        if (proxyHost != null && proxyHost.length() == 0) proxyHost = null;
        client = createClient(proxyHost, proxyPort);
        service = client.resource(UriBuilder.fromUri(baseUrl).build());
    }

    public void scan(String url) throws Exception {
        String query = "target=" + URLEncoder.encode(url, "UTF-8");
        ClientResponse response = service.path("scanner").path("scan").post(ClientResponse.class, query);
    }

    public int percentComplete() throws Exception {
        ClientResponse response = service.path("scanner").path("complete").get(ClientResponse.class);
        return response.getEntity(JSONObject.class).getInt("complete");
    }

    public ScanIssueList getIssueList() {
        return (service.path("scanner").path("issues").get(ScanIssueList.class));
    }

    public List<HttpMessage> getProxyHistory() {
        HttpMessageList result = service.path("proxy").path("history").get(HttpMessageList.class);
        return result.messages;
    }

    public List<HttpMessage> getProxyHistory(String url) throws UnsupportedEncodingException {
        HttpMessageList result = service.path("proxy").path("history").queryParam("url", url).get(HttpMessageList.class);
        return result.messages;
    }
    
    public List<HttpMessage> findInResponseHistory(String regex) throws UnsupportedEncodingException {
    	String query = "regex="+URLEncoder.encode(regex, "UTF-8");
    	HttpMessageList result = service.path("proxy").path("history").path("response").post(HttpMessageList.class,query);
    	if (result != null) return result.messages;
    	return null;
    }
    
    public List<HttpMessage> findInRequestHistory(String regex) throws UnsupportedEncodingException {
    	String query = "regex="+URLEncoder.encode(regex, "UTF-8");
    	HttpMessageList result = null;
    	try {
    		result = service.path("proxy").path("history").path("request").post(HttpMessageList.class,query);
    		if (result != null) return result.messages;
    	} catch (UniformInterfaceException uie) {
    		log.error(uie.getMessage());
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
    
    public void clearIssues() throws RuntimeException {
        service.path("clear").type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
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
