/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.continuumsecurity.burpclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author stephen
 */
public class ScanPolicy {

    public static String SQL_INJECTION_BOOLEAN = "scanner.testSQLinjectionboolean";
    public static String SQL_INJECTION_TIME = "scanner.testSQLinjectiontime";
    public static String SQL_INJECTION_ERROR = "scanner.testSQLinjectionerror";
    public static String REFLECTED_XSS = "scanner.testreflectedXSS";
    public static String COMMAND_INJECTION = "scanner.testcommandinjection";
    public static String REDIRECTION = "scanner.testredirection";
    public static String SQL_INJECTION = "scanner.testSQLinjection";
    public static String LDAP_INJECTION = "scanner.testLDAPinjection";
    public static String STORED_XSS = "scanner.teststoredXSS";
    public static String INFO_DISCLOSURE = "scanner.testinfodisclosure";
    public static String COMMAND_INJECTION_INFORMED = "scanner.testcommandinjectioninformed";
    public static String SERVER_ISSUES = "scanner.testserverissues";
    public static String COMMAND_INJECTION_BLIND = "scanner.testcommandinjectionblind";
    public static String XML_SOAP_INJECTION = "scanner.testXMLSOAPinjection";
    public static String PATH_TRAVERSAL = "scanner.testpathtraversal";
    public static String HEADER_INJECTION = "scanner.testheaderinjection";
    public static String HEADER_MANIPULATION = "scanner.testheadermanipulation";
    //Injection points
    public static String INSERT_URL_PARAMS = "scanner.inserturlparams=true";
    public static String INSERT_HEADERS = "scanner.inserthttpheaders";
    public static String INSERT_COOKIES = "scanner.insertcookies";
    public static String INSERT_PARAM_NAME = "scanner.insertparamname";
    public static String INSERT_REST_PARAMS = "scanner.insertrestparams";
    public static String INSERT_BODY_PARAMS = "scanner.insertbodyparams";
    public static String INSERT_AMF_PARAMS = "scanner.insertamfparams";
    //Passive checks
    public static String MIME = "scanner.testmime";
    public static String PARAMS = "scanner.testparams";
    public static String FORMS = "scanner.testforms";
    public static String CACHING = "scanner.testcaching";
    public static String COOKIES = "scanner.testcookies";
    public static String LINKS = "scanner.testlinks";
    public static String VIEWSTATE = "scanner.testviewstate";
    private HashMap<String, String> policy = new HashMap<String, String>();

    public ScanPolicy() {
        disableAll();
    }

    public List<String> getKeys() {
        List<String> keys = new ArrayList<String>();
        keys.add(SQL_INJECTION_BOOLEAN);
        keys.add(SQL_INJECTION_TIME);
        keys.add(SQL_INJECTION_ERROR);
        keys.add(REFLECTED_XSS);
        keys.add(COMMAND_INJECTION);
        keys.add(REDIRECTION);
        keys.add(SQL_INJECTION);
        keys.add(LDAP_INJECTION);
        keys.add(STORED_XSS);
        keys.add(INFO_DISCLOSURE);
        keys.add(COMMAND_INJECTION_INFORMED);
        keys.add(SERVER_ISSUES);
        keys.add(COMMAND_INJECTION_BLIND);
        keys.add(XML_SOAP_INJECTION);
        keys.add(PATH_TRAVERSAL);
        keys.add(HEADER_INJECTION);
        keys.add(HEADER_MANIPULATION);

        //Injection points
        keys.add(INSERT_URL_PARAMS);
        keys.add(INSERT_HEADERS);
        keys.add(INSERT_COOKIES);
        keys.add(INSERT_PARAM_NAME);
        keys.add(INSERT_REST_PARAMS);
        keys.add(INSERT_BODY_PARAMS);
        keys.add(INSERT_AMF_PARAMS);

        //Passive checks
        keys.add(MIME);
        keys.add(PARAMS);
        keys.add(FORMS);
        keys.add(CACHING);
        keys.add(COOKIES);
        keys.add(LINKS);
        keys.add(VIEWSTATE);
        return keys;
    }

    public void disableAll() {
        for (String key : getKeys()) {
            policy.put(key, "false");
        }
    }

    public void enableAll() {
        for (String key : getKeys()) {
            policy.put(key, "true");
        }
    }

    public ScanPolicy disable(String... settings) {
        for (String value : settings) {
            policy.put(value, "false");
        }
        return this;
    }

    public ScanPolicy enable(String... settings) {
        for (String value : settings) {
            policy.put(value, "true");
        }
        return this;
    }

    public String get(String key) {
        return getPolicy().get(key);
    }

    public ScanPolicy(ScanPolicy existing) {
        policy.putAll(existing.getPolicy());
    }

    public HashMap<String, String> getPolicy() {
        return policy;
    }
    
	public ScanPolicy enableAllInjectionPoints() {
		policy.put(ScanPolicy.INSERT_BODY_PARAMS, "true");
		policy.put(ScanPolicy.INSERT_AMF_PARAMS, "true");
		policy.put(ScanPolicy.INSERT_COOKIES, "true");
		policy.put(ScanPolicy.INSERT_HEADERS, "true");
		policy.put(ScanPolicy.INSERT_PARAM_NAME, "true");
		policy.put(ScanPolicy.INSERT_URL_PARAMS, "true");
		return this;
	}

	public ScanPolicy enableRESTparams() {
		policy.put(ScanPolicy.INSERT_REST_PARAMS, "true");
		return this;
	}

	public ScanPolicy enablePassive() {
		enable(ScanPolicy.FORMS,
				ScanPolicy.LINKS, ScanPolicy.PARAMS, ScanPolicy.COOKIES,
				ScanPolicy.MIME, ScanPolicy.CACHING,
				ScanPolicy.INFO_DISCLOSURE, ScanPolicy.VIEWSTATE);
		return this;
	}

	public ScanPolicy enableSQLinjection() {
		enable(ScanPolicy.SQL_INJECTION,
				ScanPolicy.SQL_INJECTION_BOOLEAN,
				ScanPolicy.SQL_INJECTION_ERROR);
		return this;
	}
	
	public ScanPolicy enableSQLinjectionTiming() {
		enable(ScanPolicy.SQL_INJECTION_TIME);
		return this;
	}

	public ScanPolicy enableLDAPinjection() {
		enable(ScanPolicy.LDAP_INJECTION)
				.getPolicy();
		return this;
	}

	public ScanPolicy enableXMLinjection() {
		enable(ScanPolicy.XML_SOAP_INJECTION)
				.getPolicy();
		return this;
	}

	public ScanPolicy enableMisc() {
		enable(ScanPolicy.PATH_TRAVERSAL,
				ScanPolicy.HEADER_INJECTION, ScanPolicy.HEADER_MANIPULATION,
				ScanPolicy.SERVER_ISSUES);
		return this;
	}

	public ScanPolicy enableXSS() {
		enable(ScanPolicy.REFLECTED_XSS,
				ScanPolicy.STORED_XSS);
		return this;
	}

	public ScanPolicy enableCommandInjection() {
		enable(ScanPolicy.COMMAND_INJECTION,
				ScanPolicy.COMMAND_INJECTION_INFORMED,
				ScanPolicy.COMMAND_INJECTION_BLIND);
		return this;
	}
	
	public ScanPolicy enableGroupByString(String name) {
		if ("command.injection".equalsIgnoreCase(name)) this.enableCommandInjection();
		if ("xss".equalsIgnoreCase(name)) this.enableXSS();
		if ("misc".equalsIgnoreCase(name)) this.enableMisc();
		if ("xml.injection".equalsIgnoreCase(name)) this.enableXMLinjection();
		if ("ldap.injection".equalsIgnoreCase(name)) this.enableLDAPinjection();
		if ("sql.injection".equalsIgnoreCase(name)) this.enableSQLinjection();
		if ("sql.injection.timing".equalsIgnoreCase(name)) this.enableSQLinjectionTiming();
		if ("rest.params".equalsIgnoreCase(name)) this.enableRESTparams();
		if ("all.injection.points".equalsIgnoreCase(name)) this.enableAllInjectionPoints();
		if ("passive".equalsIgnoreCase(name)) this.enablePassive();
		return this;
	}

}
