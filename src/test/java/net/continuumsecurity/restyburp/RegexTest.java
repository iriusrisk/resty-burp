package net.continuumsecurity.restyburp;

import java.util.regex.Pattern;

import org.junit.Test;

public class RegexTest {

	@Test
	public void testRegex() {
		String regex = "<input.*?type.*?=.*?password";
		String message = "<tr><td class=\"label loginLabel\"><label for=\"formId:password\" class=\"formLabel\">Password:</label></td><td class=\"field\"><input id=\"formId:password\" type=\"password\" name=\"formId:password\" autocomplete=\"off\" value=\"\" maxlength=\"200\" <tr>";
		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
    	assert p.matcher(message).matches();
	    
	}
}
