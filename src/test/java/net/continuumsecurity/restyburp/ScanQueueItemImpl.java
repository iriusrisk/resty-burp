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

/**
 *
 * @author stephen
 */
public class ScanQueueItemImpl implements IScanQueueItem {
    private IScanIssue[] issues;
    private int numErrors;
    private int numInsertionPoints;
    private int numRequests;
    private byte percentageComplete;
    private String status;

    /**
     * @return the issues
     */
    public IScanIssue[] getIssues() {
        return issues;
    }

    public void cancel() {}

    /**
     * @param issues the issues to set
     */
    public void setIssues(IScanIssue[] issues) {
        this.issues = issues;
    }

    /**
     * @return the numErrors
     */
    public int getNumErrors() {
        return numErrors;
    }

    /**
     * @param numErrors the numErrors to set
     */
    public void setNumErrors(int numErrors) {
        this.numErrors = numErrors;
    }

    /**
     * @return the numInsertionPoints
     */
    public int getNumInsertionPoints() {
        return numInsertionPoints;
    }

    /**
     * @param numInsertionPoints the numInsertionPoints to set
     */
    public void setNumInsertionPoints(int numInsertionPoints) {
        this.numInsertionPoints = numInsertionPoints;
    }

    /**
     * @return the numRequests
     */
    public int getNumRequests() {
        return numRequests;
    }

    /**
     * @param numRequests the numRequests to set
     */
    public void setNumRequests(int numRequests) {
        this.numRequests = numRequests;
    }

    /**
     * @return the percentageComplete
     */
    public byte getPercentageComplete() {
        return percentageComplete;
    }

    /**
     * @param percentageComplete the percentageComplete to set
     */
    public void setPercentageComplete(byte percentageComplete) {
        this.percentageComplete = percentageComplete;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    
}
