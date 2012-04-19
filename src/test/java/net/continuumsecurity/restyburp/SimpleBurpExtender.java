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
	public void applicationClosing() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newScanIssue(IScanIssue arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processHttpMessage(String arg0, boolean arg1,
			IHttpRequestResponse arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public byte[] processProxyMessage(int arg0, boolean arg1, String arg2,
			int arg3, boolean arg4, String arg5, String arg6, String arg7,
			String arg8, String arg9, byte[] arg10, int[] arg11) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerExtenderCallbacks(IBurpExtenderCallbacks arg0) {
		this.callbacks = arg0;
		
	}

	@Override
	public void setCommandLineArgs(String[] arg0) {
		// TODO Auto-generated method stub
		
	}

}
