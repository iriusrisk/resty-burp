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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.continuumsecurity.restyburp;

import burp.BurpExtender;
import burp.IHttpRequestResponse;
import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.continuumsecurity.restyburp.model.HttpMessage;
import net.continuumsecurity.restyburp.model.MessageType;
import net.continuumsecurity.restyburp.model.ScanIssueBean;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author stephen
 */
public class BurpService implements IBurpService {
	static final String WS_URI = "http://localhost:8181/";

	static Logger log = Logger.getLogger(BurpService.class.toString());
	BurpExtender extender;
	// Map of scanIds to ScanQueueMaps
	ScanQueueMap scans = new ScanQueueMap();
	int currentScanId = 0;
	static boolean headless = false;
	static String configFile = null;

    @Override
    public byte[] makeRequest(String host, int port, boolean useHttps, byte[] request) throws Exception {
        return extender.getCallbacks().makeHttpRequest(host,port,useHttps,request);
    }

    private static BurpService instance;

	private BurpService() {
		PropertyConfigurator.configure("log4j.properties");
		log.debug("Creating new burp service");
		System.setProperty("java.awt.headless", Boolean.toString(headless));
		burp.StartBurp.main(new String[0]);
        try {
            log.debug("Waiting for burp to start up...");
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        extender = BurpExtender.getInstance();
		Map<String, String> config = new HashMap<String, String>();
		config.put("proxy.interceptrequests", "false");
		updateConfig(config);
		try {
			reset();
			// System.out.println("Ready to save config in 20 seconds");
			// Thread.sleep(20000);
			// saveConfig("newconfig.burp");

		} catch (Exception ex) {
			throw new RuntimeException(
					"Could not reset with blank state file: blank.burp.state");
		}
		extender.getCallbacks().setProxyInterceptionEnabled(false);
		assert extender != null;
	}

	public ScanQueueMap getScanQueue() {
		return scans;
	}
	
	public static BurpService getInstance() {
		if (instance == null) {
			instance = new BurpService();
		}
		return instance;
	}

	public void stop() {
		scans = null;
		extender.getCallbacks().exitSuite(false);
	}

	@Override
	public Map<String, String> getConfig() {
		return extender.getCallbacks().saveConfig();
	}

	public BurpExtender getExtender() {
		return extender;
	}

	@Override
	public void setConfig(Map<String, String> newConfig) {
		extender.getCallbacks().loadConfig(newConfig);
		log.debug("New config set: " + mapToString(newConfig));
	}

	public String mapToString(Map<String, String> theMap) {
		StringBuilder sb = new StringBuilder();
		for (String key : theMap.keySet()) {
			sb.append("\n" + key + "=" + theMap.get(key));
		}
		return sb.toString();
	}

	@Override
	public void updateConfig(Map<String, String> newConfig) {
		Map<String, String> existingConfig = extender.getCallbacks()
				.saveConfig();
		existingConfig.putAll(newConfig);
		extender.getCallbacks().loadConfig(existingConfig);
		log.debug("Updated config: " + mapToString(newConfig));
	}

	@Override
	public void saveConfig(String filename) {
		FileOutputStream out = null;
		try {
			Map<String, String> config = extender.getCallbacks().saveConfig();
			out = new FileOutputStream(filename);
			Properties props = new Properties();
			props.putAll(config);
			props.store(out, "Saved: " + new Date().toString());
			out.close();
			log.debug("Config saved to: " + filename);
		} catch (Exception ex) {
			// TODO Fix this catchall block
			Logger.getLogger(BurpExtender.class.getName()).error(ex);
		} finally {
			try {
				out.close();
			} catch (IOException ex) {
				Logger.getLogger(BurpExtender.class.getName()).error(ex);
			}
		}
	}

	@Override
	public void loadConfig(String filename) {
		FileInputStream in = null;
		try {
			extender.setConfigFile(filename);
			in = new FileInputStream(filename);
			Properties props = new Properties();
			props.load(in);
            Map<String,String> map = new HashMap<String,String>();
            for (Object key : props.keySet()) {
                String stringKey = (String)key;
                map.put(stringKey,props.getProperty(stringKey));
            }
			extender.getCallbacks().loadConfig(map);
			in.close();
			log.debug("Config loaded from: " + filename);
		} catch (Exception ex) {
			// TODO Fix this catchall block
			Logger.getLogger(BurpExtender.class.getName()).error(ex);
		} finally {
			try {
				in.close();
			} catch (IOException ex) {
				Logger.getLogger(BurpExtender.class.getName()).error(ex);
			}
		}
	}

	@Override
	public synchronized void scan(String target) throws MalformedURLException {
		URL targetUrl = new URL(target);
		currentScanId++;
		scans = extender.scan(targetUrl.toExternalForm());
	}

	/*
	 * Returns true if the url is in the queue or has already been scanned
	 * during this session
	 */
	public boolean alreadyScanned(String target) {
		log.debug(" Checking if already scanned: " + target);

		for (String url : scans.getUrls()) {
			log.debug("  already scanned: " + url);
			if (url.equalsIgnoreCase(target)) {
				log.debug(" Already scanned.");
				return true;
			}
		}

		log.debug(" Not already scanned");
		return false;
	}

	@Override
	public int getPercentageComplete() {
		log.debug("Getting percentage complete.");
		return scans.getPercentageComplete();
	}

	@Override
	public List<ScanIssueBean> getIssues() {
		return extender.getIssueList().getIssues();
		/*
		 * log.debug("Getting SanQueueMap for scanId: "+scanId); ScanQueueMap
		 * sqm = scans.get(scanId); if (sqm == null) { throw new
		 * ScanNotFoundException("Scan ID not found: " + scanId); }
		 * log.debug("Found: "+sqm.getIssues().size()+" issues."); return
		 * sqm.getIssues();
		 */
	}

	/*
	 * Matches using the following regex options: Pattern.compile(regex,
	 * Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE) Searches
	 * in reverse order from most recent message to oldest
	 */
	@Override
	public List<HttpMessage> findInHistory(String regex, MessageType type) {
		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE
				| Pattern.DOTALL | Pattern.MULTILINE);
		List<HttpMessage> result = new ArrayList<HttpMessage>();
		List<HttpMessage> proxyHistory = getProxyHistory();
		for (int i = proxyHistory.size() - 1; i >= 0; i--) {
			String stringBean = "";
			HttpMessage bean = proxyHistory.get(i);

			try {
				// TODO read encoding from headers
				// TODO Bug here, for some long pages (>40 000 BYTES) the
				// matcher hangs.
				if (type == MessageType.REQUEST && bean.getRequest() != null) {
					stringBean = new String(bean.getRequest(), "UTF8");
				}
				if (type == MessageType.RESPONSE && bean.getResponse() != null) {
					stringBean = new String(bean.getResponse(), "UTF8");
				}
				log.debug("Searching in message to: " + bean.getUrl());
				Matcher m = p.matcher(stringBean);
				log.trace("Searching in message: " + bean.getUrl());
				if (m.find()) {
					log.trace("Found regex: " + regex);
					result.add(bean);
				} else {
					log.trace("Did not find regex: " + regex);
				}
			} catch (UnsupportedEncodingException e) {
				log.error(e.getMessage());
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public List<HttpMessage> getProxyHistory() {
		List<HttpMessage> result = new ArrayList<HttpMessage>();
		for (IHttpRequestResponse ihrr : extender.getProxyHistory()) {
			result.add(new HttpMessage(ihrr));
		}
		log.debug("Returning " + result.size()
				+ " request/responses from the proxy history.");
		return result;
	}

	@Override
	public List<HttpMessage> getProxyHistory(String url) throws Exception {
		List<HttpMessage> result = new ArrayList<HttpMessage>();
		for (IHttpRequestResponse ihrr : extender.getProxyHistory()) {
			if (ihrr.getUrl().sameFile(new URL(url))) {
				result.add(new HttpMessage(ihrr));
			}
		}
		return result;
	}

	@Override
	public void reset() throws Exception {
		extender.reset();
		clearIssues();
		log.debug("Burp state reset.");
		log.debug(this.getIssues().size() + " issues after reset.");
	}
	
	@Override
	public void clearIssues() throws Exception {
		scans.clear();
		extender.getIssueList().getIssues().clear();
	}

	public void startRESTServer() {
		final Map<String, String> initParams = new HashMap<String, String>();
		initParams.put("com.sun.jersey.config.property.packages",
				"net.continuumsecurity.restyburp.server");
		// Using JAXB mapping method
		// initParams.put("com.sun.jersey.api.json.POJOMappingFeature", "true");
		log.debug("Starting REST server");
		new Thread() {

			public void run() {
				try {
					SelectorThread threadSelector = GrizzlyWebContainerFactory
							.create(WS_URI, initParams);
					log.debug(String
							.format("Jersey app started with WADL available at %sapplication.wadl\n",
									WS_URI, WS_URI));
				} catch (IOException ex) {
					ex.printStackTrace();
				} catch (IllegalArgumentException ex) {
					ex.printStackTrace();
				}
			}
		}.start();
	}

	public static void main(String... args) {
		OptionParser parser = new OptionParser();
		parser.accepts("f").withOptionalArg().ofType(String.class);
		parser.accepts("g");
		OptionSet options = parser.parse(args);

		if (options.has("g")) {
			headless = false;
		}

		BurpService bs = BurpService.getInstance();

		if (options.has("f")) {
			if (options.hasArgument("f")) {
				bs.loadConfig((String) options.valueOf("f"));
			} else {
				bs.loadConfig("burp.config");
			}
		}

		bs.startRESTServer();
	}
}
