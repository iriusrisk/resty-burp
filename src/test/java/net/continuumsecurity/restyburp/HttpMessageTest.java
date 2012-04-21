package net.continuumsecurity.restyburp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.junit.Assert.*;

import net.continuumsecurity.restyburp.model.HttpMessage;
import net.continuumsecurity.restyburp.model.MessageType;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

public class HttpMessageTest {
    static Logger log = Logger.getLogger(HttpMessageTest.class.toString());
    List<HttpMessage> dummyHistory = new ArrayList<HttpMessage>();
	String source = null;

	@Before
	public void setup() {
		File file = new File("src/test/java/longhtmlfile.html");
		StringBuffer contents = new StringBuffer();
		String line;
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(file));

			// repeat until all lines is read
			while ((line = reader.readLine()) != null) {
				contents.append(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		source = contents.toString();
		HttpMessage dummy = new HttpMessage();
		dummy.setResponse(source.getBytes());
		dummyHistory.add(dummy);
		
	}
	
	//@Test
	public void testStringMatching() {
		String regex = ".*input.*?type.*?=.*?password.*";
		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
        assert(p.matcher(source).matches());
	}
	
    public List<HttpMessage> findInHistory(String regex, MessageType type) {
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
    	List<HttpMessage> result = new ArrayList<HttpMessage>();
        for (HttpMessage bean : dummyHistory) {
        	String stringBean;
			try {
				//TODO read encoding from headers
				//TODO Bug here, for some long pages (>40 000 BYTES) the matcher hangs.
				stringBean = null;
				if (type == MessageType.REQUEST) stringBean = new String(bean.getRequest(),"UTF8");
				if (type == MessageType.RESPONSE) stringBean = new String(bean.getResponse(),"UTF8");
				log.debug("Searching in message to: "+bean.getUrl());
				Matcher m = p.matcher(stringBean);
				log.trace("Searching in message: "+bean.getUrl());
	            if (m.find()) {
	            	log.trace("Found regex: "+regex);
	            	result.add(bean);
	            } else {
	            	log.trace("Did not find regex: "+regex);
	            }
			} catch (UnsupportedEncodingException e) {
				log.error(e.getMessage());
				e.printStackTrace();
			}
        }
        log.debug("Did not find regex: "+regex);
        return result;
    }
	
	@Test
	public void testByteMatching() {
		String regex = "(?i)input[\\s\\w=:'\"]*type\\s*=\\s*['\"]password['\"]";
		List<HttpMessage> result = findInHistory(regex,MessageType.RESPONSE);
		assertEquals(result.size(),1);
	}
	
}
