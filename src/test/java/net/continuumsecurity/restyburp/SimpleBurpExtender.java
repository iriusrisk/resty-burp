package net.continuumsecurity.restyburp;

import org.apache.log4j.Logger;

import burp.BurpExtender;
import burp.IBurpExtender;
import burp.IBurpExtenderCallbacks;
import burp.IHttpRequestResponse;
import burp.IScanIssue;

public class SimpleBurpExtender implements IBurpExtender {
    static Logger log = Logger.getLogger(BurpExtender.class.toString());
    private IBurpExtenderCallbacks callbacks;

	@Override
	public void registerExtenderCallbacks(IBurpExtenderCallbacks arg0) {
		this.callbacks = arg0;
		
	}

}
