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
package net.continuumsecurity.restyburp.server;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import net.continuumsecurity.restyburp.model.HttpRequestResponseBean;
import net.continuumsecurity.restyburp.model.ProxyHistoryList;
import net.continuumsecurity.restyburp.model.ScanIssueBean;
import net.continuumsecurity.restyburp.model.ScanIssueList;

@Provider
public class JAXBContextResolver implements ContextResolver<JAXBContext> {
 
     private JAXBContext context;
     private Class[] types = {ProxyHistoryList.class,ScanIssueList.class,ScanIssueBean.class,HttpRequestResponseBean.class};
 
     public JAXBContextResolver() throws Exception {
         this.context = 
 	  new JSONJAXBContext(JSONConfiguration.natural().build(), types);
     }
 
     public JAXBContext getContext(Class<?> objectType) {
         for (Class type : types) {
             if (type == objectType) {
                 return context;
             }
         }
         return null;
     }
 }
