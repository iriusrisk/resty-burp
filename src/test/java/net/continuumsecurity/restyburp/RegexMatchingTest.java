package net.continuumsecurity.restyburp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

public class RegexMatchingTest {
	String source = null;

	@Before
	public void setup() {
		File file = new File("src/test/java/longhtmlfile.html");
		StringBuffer contents = new StringBuffer();
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(file));

			// repeat until all lines is read
			while ((source = reader.readLine()) != null) {
				contents.append(source);
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
	}
	
	@Test
	public void testSimpleMatching() {
		String regex = "password.*";
		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

        assert(p.matcher(source).matches());
	}
	
	@Test
	public void testLongMatching() {
		String regex = ".*input.*?type.*?=.*?password.*";
		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
        assert(p.matcher(source).matches());
	}
	

}
