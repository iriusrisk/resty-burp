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

import burp.IScanIssue;
import burp.IScanQueueItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.continuumsecurity.restyburp.model.ScanIssueBean;
import org.apache.log4j.Logger;

/**
 *
 * Map indexed by URL of a List of IScanQueueItems.
 */
public class ScanQueueMap {
    static Logger log = Logger.getLogger(ScanQueueMap.class.toString());

    private Map<String, List<IScanQueueItem>> map = new HashMap<String, List<IScanQueueItem>>();

    public ScanQueueMap() {
    }

    public void addItem(String url, IScanQueueItem scanItem) {
        if (map.containsKey(url)) {
            List<IScanQueueItem> list = map.get(url);
            assert list != null;
            list.add(scanItem);
        } else {
            List<IScanQueueItem> list = new ArrayList<IScanQueueItem>();
            list.add(scanItem);
            map.put(url, list);
        }
    }

    public boolean hasUrl(String url) {
        log.debug(" Checking whether map.hasUrl("+url+")");
        for (String theUrl : map.keySet()) {
            log.trace("  found: "+theUrl);
            if (theUrl.equalsIgnoreCase(url)) return true;
        }
        log.trace(" Not found.");
        return false;
    }
    
    public Set<String> getUrls() {
        return map.keySet();
    }

    public List<IScanQueueItem> getQueue(String url) {
        return map.get(url);
    }

    public List<ScanIssueBean> getIssues() {
        List<ScanIssueBean> issues = new ArrayList<ScanIssueBean>();

        for (String key : getUrls()) {
            log.debug(" Getting issues for: "+key);
            for (IScanQueueItem scanQueue : getQueue(key)) {
                log.debug(" ScanQueueItem: "+scanQueue.toString());
                for (IScanIssue issue : scanQueue.getIssues()) {
                    log.debug("  Adding issue: " + issue.getIssueName());
                    issues.add(new ScanIssueBean(issue));
                }
            }
        }
        return issues;
    }

    public int getPercentageComplete() {
        if (map.keySet().size() == 0) return 100;
        int numItems = 0;
        int total = 0;
        for (String key : map.keySet()) {
            for (IScanQueueItem scanQueue : getQueue(key)) {
                numItems++;
                total += scanQueue.getPercentageComplete();
            }
        }
        return total / numItems;
    }

    //Utility method not meant to be exposed to through REST
    public void waitForAllToComplete() {
        boolean completed = false;
        while (!completed) {
            completed = true;
            for (String key : map.keySet()) {
                for (IScanQueueItem scanQueue : getQueue(key)) {
                    if (scanQueue.getPercentageComplete() < 100) {
                        completed = false;
                        log.debug(" " + key + " Status: " + scanQueue.getStatus() + " " + scanQueue.getPercentageComplete());
                    } 
                }
            }
            try {
                Thread.sleep(5);
            } catch (InterruptedException ex) {
                Logger.getLogger(ScanQueueMap.class.getName()).error(ex);
            }
        }
    }
}
