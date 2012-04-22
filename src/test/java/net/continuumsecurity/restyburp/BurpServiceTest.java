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

import net.continuumsecurity.burpclient.ScanPolicy;
import net.continuumsecurity.restyburp.model.ScanIssueBean;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openqa.selenium.By;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;


/*
 * Not a proper JUnit test, having problems loading Jersey/Burp through JUnit, for now just using the main method to test manually
 */
public class BurpServiceTest {
    static Logger log = Logger.getLogger(BurpServiceTest.class.toString());
    HtmlUnitDriver driver;
    static IBurpService burp;
    Settings settings;
    final String HOMEPAGE = "http://localhost:9110/ropeytasks/";
    final String LOGINPAGE = HOMEPAGE + "user/login";
    final String SEARCHPAGE = HOMEPAGE + "task/search?q=test&search=Search";
    int proxyPort = 8080;

    public BurpServiceTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        //burp = BurpService.getInstance();
		PropertyConfigurator.configure("log4j.properties");
        burp = BurpService.getInstance();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    	log.debug("Test case done.");
    }

    @Before
    public void setUp() {
        driver = new HtmlUnitDriver();
        driver.setProxy("127.0.0.1", proxyPort);
        try {
			burp.reset();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

  /*  @Test
    public void testFindInResponse() {
    	String regex = ".*password.*";
    	driver.get(target);
    	driver.get(target+"user/login");
    	HttpMessage result = burp.findInHistory(regex,MessageType.RESPONSE);
    	assert (new String(result.getResponse()).contains(regex));
    	log.debug("Test done");
    }*/
    
    public void login(String username,String password) {
		driver.findElement(By.id("username")).clear();
		driver.findElement(By.id("username")).sendKeys(username);
		driver.findElement(By.id("password")).clear();
		driver.findElement(By.id("password")).sendKeys(password);
		driver.findElement(By.name("_action_login")).click();
	}
    
    @Test
    public void testPassiveScan() throws Exception {
        ScanQueueMap sq;
        driver.get(HOMEPAGE);
    	driver.get(LOGINPAGE);
    	login("bob","password");
    	driver.get(SEARCHPAGE);
    	burp.setConfig(new ScanPolicy().enablePassive().getPolicy());
        burp.scan(HOMEPAGE);
        //for (int i=0;i <10;i++) {
        while (burp.getPercentageComplete() < 100) {
            log.debug("Complete: "+burp.getPercentageComplete());
            Thread.sleep(2000);
        }
        for (ScanIssueBean issue: burp.getIssues()) {
        	log.debug("Issue: "+issue.getIssueName()+" in "+issue.getUrl());
        }
        assertEquals(2,burp.getIssues().size());
    }
    
    @Test
    public void testXSSScan() throws Exception {
        ScanQueueMap sq;
        driver.get(HOMEPAGE);
    	driver.get(LOGINPAGE);
    	login("bob","password");
    	driver.get(SEARCHPAGE);
    	burp.setConfig(new ScanPolicy().enableXSS().getPolicy());
        burp.scan(SEARCHPAGE);
        //for (int i=0;i <10;i++) {
        while (burp.getPercentageComplete() < 100) {
            log.debug("Complete: "+burp.getPercentageComplete());
            Thread.sleep(2000);
        }
        for (ScanIssueBean issue: burp.getIssues()) {
        	log.debug("Issue: "+issue.getIssueName()+" in "+issue.getUrl());
        }
        assertEquals(1,burp.getIssues().size());
    }
    
    /*
    public static void main (String... args) {
        try {
            BurpServiceTest bst = new BurpServiceTest();
            setUpClass();
            bst.setUp();
            bst.testFindInResponse();
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        
    }*/
}
