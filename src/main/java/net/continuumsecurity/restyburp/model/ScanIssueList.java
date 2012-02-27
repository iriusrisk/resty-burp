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

import java.util.List;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author stephen
 */
@XmlRootElement
public class ScanIssueList {
    private List<ScanIssueBean> issues;
    
    @XmlElementWrapper(name = "issues")
    public List<ScanIssueBean> getIssues() {
        return issues;
    }
    
    public ScanIssueList filterBySeverity(String severity) {
        ScanIssueList result = new ScanIssueList();
        for (ScanIssueBean issue : issues) {
            if (severity.equalsIgnoreCase(issue.getSeverity())) {
                result.getIssues().add(issue);
            }
        }
        return result;
    }

    public void setIssues(List<ScanIssueBean> all) {
        this.issues = all;
    }
    
    
}
