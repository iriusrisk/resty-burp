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

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/*
 * Not a proper JUnit test, having problems loading Jersey/Burp through JUnit, for now just using the main method to test manually
 */
public class BurpServiceTest {
    static Logger log = Logger.getLogger(BurpServiceTest.class.toString());
    HtmlUnitDriver driver;
    static IBurpService burp;
    Settings settings;
    final String target = "http://localhost:9110/ropeytasks/user/login";
    int proxyPort = 8080;

    public BurpServiceTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        //burp = BurpService.getInstance();
        burp = BurpService.getInstance();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        driver = new HtmlUnitDriver();
        driver.setProxy("127.0.0.1", proxyPort);
    }

    @After
    public void tearDown() {
        while (true) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                log.error(ex);
            }
        }
    }

    
    public void testScanQueue() throws Exception {
        ScanQueueMap sq;
        driver.get(target);
        int scanId = burp.scan(target);
        log.debug(">>> New scan with scanID="+scanId);
        while (burp.getPercentageComplete(scanId) < 100) {
            log.debug("Complete: "+burp.getPercentageComplete(scanId));
            Thread.sleep(2000);
        }
        log.debug("All completed.");
    }
    
    public static void main (String... args) {
        try {
            BurpServiceTest bst = new BurpServiceTest();
            setUpClass();
            bst.setUp();
            bst.testScanQueue();
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        
    }
}
