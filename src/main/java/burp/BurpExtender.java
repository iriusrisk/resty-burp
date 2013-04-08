/*******************************************************************************
 * BDD-Security, application security testing framework
 * 
 * Copyright (C) `2012 Stephen de Vries`
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see `<http://www.gnu.org/licenses/>`.
 ******************************************************************************/
package burp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import net.continuumsecurity.burpclient.ScanPolicy;
import net.continuumsecurity.restyburp.ScanQueueMap;
import net.continuumsecurity.restyburp.Utils;
import net.continuumsecurity.restyburp.model.ScanIssueBean;
import net.continuumsecurity.restyburp.model.ScanIssueList;

import org.apache.log4j.Logger;

/*
 * Can only handle 1 scan at a time, have to perform resets() between scans to clear the issues.
 */
public class BurpExtender implements IBurpExtender {
    static Logger log = Logger.getLogger(BurpExtender.class.toString());
    private IBurpExtenderCallbacks callbacks;
    private Set<URL> outOfScope = new HashSet<URL>();
    private static BurpExtender instance;
    private String configFile = null;
    private ScanIssueList issueList = new ScanIssueList();

    //Wait for the instance to be initialised
    public static BurpExtender getInstance() {
        return instance;
    }
    // Called to handle command line arguments passed to Burp

    public void setCommandLineArgs(String[] args) {
    }

    //This function is called once when Burp Suite loads
    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        this.callbacks = callbacks;
        callbacks.setExtensionName("Resty-Burp extension");
        instance = this;
        log.debug("Burp extender initialised.");
    }

    // Called each time a HTTP request or HTTP reply is generated from a Burp tool
    public void processHttpMessage(
            String toolName,
            boolean messageIsRequest,
            IHttpRequestResponse messageInfo) {
    }

    public IHttpRequestResponse[] getProxyHistory() {
        return callbacks.getProxyHistory();
    }
    
    public void reset() throws Exception {
    	issueList.getIssues().clear();
        callbacks.restoreState(new File("blank.burp.state"));
    }     

    public ScanQueueMap scan(String baseUrl) {
        log.debug("Attempting to scan all: " + baseUrl);
        ScanQueueMap map = new ScanQueueMap();
 
        for (IHttpRequestResponse rr : callbacks.getProxyHistory()) {
            try {
                URL url = new URL(baseUrl);
                if (Utils.containsUrl(rr.getUrl(), url) && !outOfScope.contains(rr.getUrl()) && !map.hasUrl(rr.getUrl().toExternalForm())) {
                    log.debug("\tcallbacks.isInScope("+rr.getUrl()+") is "+callbacks.isInScope(rr.getUrl()));
                    if (!callbacks.isInScope(rr.getUrl())) {
                    	callbacks.includeInScope(rr.getUrl());
                    } 
                    boolean useHttps = rr.getProtocol().equalsIgnoreCase("https");
                    log.debug("\tabout to scan: "+rr.getUrl());
                    IScanQueueItem isq = callbacks.doActiveScan(rr.getHost(), rr.getPort(), useHttps, rr.getRequest());
                    log.debug("ScanItem for "+rr.getUrl()+" has "+isq.getNumRequests()+" requests, "+isq.getNumInsertionPoints()+" insertion points, status: "+isq.getStatus());
                    log.debug("\tadding "+rr.getUrl().toExternalForm()+" to ScanQueueMap");
                    map.addItem(rr.getUrl().toExternalForm(), isq);
                    if (isPassiveEnabled()) callbacks.doPassiveScan(rr.getHost(), rr.getPort(), useHttps, rr.getRequest(),rr.getResponse());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public void removeFromScope(URL url) {
        outOfScope.add(url);
    }

    public void newScanIssue(IScanIssue issue) {
        log.debug("Found issue: " + issue.getIssueName()+" in Url: "+issue.getUrl());
        issueList.getIssues().add(new ScanIssueBean(issue));
    }
    
    public IBurpExtenderCallbacks getCallbacks() {
        return callbacks;
    }
    
    public ScanIssueList getIssueList() {
    	return issueList;
    }
    
    // Called during proxy requests, not needed with processHttpMessage
    public byte[] processProxyMessage(
            int messageReference,
            boolean messageIsRequest,
            String remoteHost,
            int remotePort,
            boolean serviceIsHttps,
            String httpMethod,
            String url,
            String resourceType,
            String statusCode,
            String responseContentType,
            byte[] message,
            int[] interceptAction) {

        return message;
    }

    // Called when application is closed
    public void applicationClosing() {
        if (configFile != null) {
            saveConfig(configFile);
        }
    }
    
    public void setConfigFile(String filename) {
        configFile = filename;
    }
    
    public boolean isPassiveEnabled() {
    	Map<String, String> config = callbacks.saveConfig();
        if (config.get(ScanPolicy.MIME).equalsIgnoreCase("true")) return true;
        if (config.get(ScanPolicy.PARAMS).equalsIgnoreCase("true")) return true;
        if (config.get(ScanPolicy.FORMS).equalsIgnoreCase("true")) return true;
        if (config.get(ScanPolicy.CACHING).equalsIgnoreCase("true")) return true;
        if (config.get(ScanPolicy.COOKIES).equalsIgnoreCase("true")) return true;
        if (config.get(ScanPolicy.LINKS).equalsIgnoreCase("true")) return true;
        if (config.get(ScanPolicy.VIEWSTATE).equalsIgnoreCase("true")) return true;
        return false;
    }
    
    private void saveConfig(String filename) {
        FileOutputStream out = null;
        try {
            Map<String, String> config = callbacks.saveConfig();
            out = new FileOutputStream(filename);
            Properties props = new Properties();
            props.putAll(config);
            props.store(out, "Saved: " + new Date().toString());
            out.close();
            log.debug("Config saved to: " + filename);
        } catch (Exception ex) {
            //TODO Fix this catchall block
            Logger.getLogger(BurpExtender.class.getName()).error(ex);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(BurpExtender.class.getName()).error(ex);
            }
        }
    }
}
