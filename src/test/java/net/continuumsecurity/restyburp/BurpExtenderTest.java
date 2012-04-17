package net.continuumsecurity.restyburp;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import burp.BurpExtender;

public class BurpExtenderTest {
	BurpExtender extender;
	URL url = null;
	
	@Before
	public void setup() {
		try {
			url = new URL("http://localhost:9110/ropeytasks/admin/list");
			burp.StartBurp.main(new String[0]);
			extender = BurpExtender.getInstance();
			assert extender != null;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testInScopeScanning() {
		try {
			System.out.println(extender.getCallbacks().isInScope(url));
			System.out.println("Adding ["+url.toExternalForm()+"] to scope.");
			extender.getCallbacks().includeInScope(url);
			System.out.println("Is in scope: "+extender.getCallbacks().isInScope(url));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
