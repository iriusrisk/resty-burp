/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.continuumsecurity.burpclient;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;

/**
 *
 * @author stephen
 */
public class ScanPolicyTest {
    
    public ScanPolicyTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
 
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testAddingPolicies() {
        ScanPolicy sp = new ScanPolicy();
        Assert.assertEquals("false",sp.get(ScanPolicy.SQL_INJECTION));
        sp.enable(ScanPolicy.SQL_INJECTION);
        Assert.assertEquals("true",sp.get(ScanPolicy.SQL_INJECTION));
        
        ScanPolicy sp2 = new ScanPolicy();
        sp2.enable(ScanPolicy.SQL_INJECTION,ScanPolicy.REFLECTED_XSS);
        Assert.assertEquals("true",sp2.get(ScanPolicy.SQL_INJECTION));
        Assert.assertEquals("true",sp2.get(ScanPolicy.REFLECTED_XSS));
        
        
        Assert.assertEquals("false",sp2.disable(ScanPolicy.REFLECTED_XSS).get(ScanPolicy.REFLECTED_XSS));
    }
}
