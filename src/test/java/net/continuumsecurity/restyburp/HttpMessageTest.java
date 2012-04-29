package net.continuumsecurity.restyburp;

import net.continuumsecurity.restyburp.model.HttpMessage;
import net.continuumsecurity.restyburp.model.MessageType;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

public class HttpMessageTest {
    static Logger log = Logger.getLogger(HttpMessageTest.class.toString());
    List<HttpMessage> dummyHistory = new ArrayList<HttpMessage>();
	String source = null;

	@Before
	public void setup() {
		source = readFileToString("src/test/java/longhtmlfile.html");
		HttpMessage dummy = new HttpMessage();
		dummy.setResponse(source.getBytes());
		dummyHistory.add(dummy);
	}

    private String readFileToString(String fileName) {
        File file = new File(fileName);
        StringBuffer contents = new StringBuffer();
        String line;
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));

            // repeat until all lines is read
            while ((line = reader.readLine()) != null) {
                contents.append(line).append("\r\n");
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
        return contents.toString();
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
	
	//@Test
	public void testByteMatching() {
		String regex = "(?i)input[\\s\\w=:'\"]*type\\s*=\\s*['\"]password['\"]";
		List<HttpMessage> result = findInHistory(regex,MessageType.RESPONSE);
		assertEquals(result.size(),1);
	}

    @Test
    public void testReplaceCookies() {
        HttpMessage message = new HttpMessage();
        message.setRequest(readFileToString("src/test/java/examplePOSTRequest.txt").getBytes());
        Map<String,String> cookieMap = new HashMap<String,String>();
        cookieMap.put("1","3");
        cookieMap.put("JSESSIONID","bob");
        message.replaceCookies(cookieMap);
        System.out.println(new String(message.getRequest()));
    }
	
}
