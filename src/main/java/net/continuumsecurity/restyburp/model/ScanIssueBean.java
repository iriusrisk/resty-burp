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
package net.continuumsecurity.restyburp.model;

import burp.IHttpRequestResponse;
import burp.IScanIssue;
import java.net.URL;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ScanIssueBean implements IScanIssue {
    private String host;
    private String issueBackground;
    private String issueDetail;
    protected String issueName;
    protected int port;
    protected String protocol;
    protected String remediationBackground;
    private URL url;
    protected String remediationDetail;
    private HttpRequestResponseBean[] httpMessages;
    protected String severity;

    public ScanIssueBean() {
    }

    public ScanIssueBean(IScanIssue theIssue) {
        host = theIssue.getHost();
        issueBackground = theIssue.getIssueBackground();
        issueDetail = theIssue.getIssueDetail();
        issueName = theIssue.getIssueName();
        port = theIssue.getPort();
        protocol = theIssue.getProtocol();
        remediationBackground = theIssue.getRemediationBackground();
        url = theIssue.getUrl();
        remediationDetail = theIssue.getRemediationDetail();
        httpMessages = new HttpRequestResponseBean[theIssue.getHttpMessages().length];
        for (int i=0;i <= theIssue.getHttpMessages().length-1;i++) {
            httpMessages[i] = new HttpRequestResponseBean(theIssue.getHttpMessages()[i]);
        }
        severity = theIssue.getSeverity();
    }

    public String getHost() {
        return host;
    }

    /**
     * Set the value of host
     *
     * @param host new value of host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Get the value of IssueBackground
     *
     * @return the value of IssueBackground
     */
    public String getIssueBackground() {
        return issueBackground;
    }

    /**
     * Set the value of IssueBackground
     *
     * @param IssueBackground new value of IssueBackground
     */
    public void setIssueBackground(String issueBackground) {
        this.issueBackground = issueBackground;
    }

    public String getIssueDetail() {
        return issueDetail;
    }

    public void setIssueDetail(String issueDetail) {
        this.issueDetail = issueDetail;
    }

    /**
     * Get the value of issueName
     *
     * @return the value of issueName
     */
    public String getIssueName() {
        return issueName;
    }

    /**
     * Set the value of issueName
     *
     * @param issueName new value of issueName
     */
    public void setIssueName(String issueName) {
        this.issueName = issueName;
    }

    /**
     * Get the value of port
     *
     * @return the value of port
     */
    public int getPort() {
        return port;
    }

    /**
     * Set the value of port
     *
     * @param port new value of port
     */
    public void setPort(int port) {
        this.port = port;
    }
    private String confidence;

    /**
     * Get the value of confidence
     *
     * @return the value of confidence
     */
    public String getConfidence() {
        return confidence;
    }

    /**
     * Get the value of protocol
     *
     * @return the value of protocol
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Set the value of protocol
     *
     * @param protocol new value of protocol
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * Get the value of remediationBackground
     *
     * @return the value of remediationBackground
     */
    public String getRemediationBackground() {
        return remediationBackground;
    }

    /**
     * Set the value of remediationBackground
     *
     * @param remediationBackground new value of remediationBackground
     */
    public void setRemediationBackground(String remediationBackground) {
        this.remediationBackground = remediationBackground;
    }

    /**
     * Get the value of remediationDetail
     *
     * @return the value of remediationDetail
     */
    public String getRemediationDetail() {
        return remediationDetail;
    }

    /**
     * Set the value of remediationDetail
     *
     * @param remediationDetail new value of remediationDetail
     */
    public void setRemediationDetail(String remediationDetail) {
        this.remediationDetail = remediationDetail;
    }

    /**
     * Get the value of severity
     *
     * @return the value of severity
     */
    public String getSeverity() {
        return severity;
    }

    /**
     * Set the value of severity
     *
     * @param severity new value of severity
     */
    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    /**
     * Set the value of confidence
     *
     * @param confidence new value of confidence
     */
    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    /**
     * @return the httpMessages
     */
    public HttpRequestResponseBean[] getHttpMessages() {
        return httpMessages;
    }

    /**
     * @param httpMessages the httpMessages to set
     */
    public void setHttpMessages(HttpRequestResponseBean[] httpMessages) {
        this.httpMessages = httpMessages;
    }
}
