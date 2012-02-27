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


import burp.IScanQueueItem;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author stephen
 */
public class ScanQueueMapTest {
    ScanQueueMap sqm;
    static String ONE;
    static String TWO;

    public ScanQueueMapTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        sqm = new ScanQueueMap();
        ScanQueueItemImpl sqi1 = new ScanQueueItemImpl();
        sqi1.setStatus("one");
        ScanQueueItemImpl sqi2 = new ScanQueueItemImpl();
        sqi2.setStatus("two");
        try {
            ONE = "http://one.com";
            TWO = "http://two.com";
            sqm.addItem(ONE, sqi1);
            sqm.addItem(ONE, sqi2);
            sqm.addItem(TWO, sqi2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testAddItem() {
        Assert.assertEquals(2, sqm.getQueue(ONE).size());
        Assert.assertEquals(1, sqm.getQueue(TWO).size());
        IScanQueueItem isq = sqm.getQueue(ONE).get(0);
        Assert.assertEquals(isq.getStatus(), "one");
    }
    

}
