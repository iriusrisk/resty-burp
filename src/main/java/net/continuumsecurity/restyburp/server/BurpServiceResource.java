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

import java.net.MalformedURLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;

import net.continuumsecurity.restyburp.BurpService;
import net.continuumsecurity.restyburp.IBurpService;
import net.continuumsecurity.restyburp.model.Config;
import net.continuumsecurity.restyburp.model.HttpMessage;
import net.continuumsecurity.restyburp.model.HttpMessageList;
import net.continuumsecurity.restyburp.model.MessageType;
import net.continuumsecurity.restyburp.model.ScanIssueList;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/*
 * POST /scan/new url returns scanid
 * GET /scan/id
 */
@Path("/")
public class BurpServiceResource {

    static Logger log = Logger.getLogger(BurpServiceResource.class.toString());
    private IBurpService burp = BurpService.getInstance();

    @POST
    @Path("scanner/scan")
    @Produces(MediaType.APPLICATION_JSON)
    public Response scan(@FormParam("target") String target) {
        log.debug("Starting scan of: " + target);
        JSONObject id;
        try {
            int scanId = burp.scan(target);
            id = new JSONObject().put("id", scanId);
        } catch (JSONException ex) {
            throw new ServerErrorException(Response.Status.INTERNAL_SERVER_ERROR, ex.getMessage());
        } catch (MalformedURLException mue) {
            throw new ServerErrorException(Response.Status.INTERNAL_SERVER_ERROR, mue.getMessage());
        }
        return Response.ok().entity(id).build();
    }

    @GET
    @Path("scanner/{scanId}/complete")
    @Produces(MediaType.APPLICATION_JSON)
    public Response percentComplete(@PathParam("scanId") int scanId) {
        //log.debug("Fetching percentage complete of: "+scanId);
        JSONObject complete;
        try {
            complete = new JSONObject().put("complete", burp.getPercentageComplete(scanId));
            
        } catch (JSONException ex) {
            throw new ServerErrorException(Response.Status.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
        return Response.ok().entity(complete).build();
    }

    @GET
    @Path("scanner/{scanId}/issues")
    @Produces(MediaType.APPLICATION_JSON)
    public ScanIssueList getIssues(@PathParam("scanId") int scanId) {
        log.debug("Fetching issues for: "+scanId);
        try {
            ScanIssueList il = new ScanIssueList();
            il.setIssues(burp.getIssues(scanId));
            log.debug(" "+il.getIssues().size()+" issues found.");

            return il;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServerErrorException(Response.Status.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }
    
    @POST
    @Path("proxy/history/response")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpMessageList searchResponse(@FormParam("regex") String search) {
    	HttpMessageList result = new HttpMessageList();
        log.debug("Searching for: "+search);
        try {
            result.setMessages(burp.findInHistory(search,MessageType.RESPONSE));
        } catch (Exception ex) {
        	ex.printStackTrace();
            throw new ServerErrorException(Response.Status.INTERNAL_SERVER_ERROR, ex.getMessage());
        } 
        return result;
    }
    
    @POST
    @Path("proxy/history/request")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpMessageList searchRequest(@FormParam("regex") String search) {
    	HttpMessageList result = new HttpMessageList();
        log.debug("Searching for: "+search);
        try {
            result.setMessages(burp.findInHistory(search,MessageType.REQUEST));
        } catch (Exception ex) {
        	ex.printStackTrace();
            throw new ServerErrorException(Response.Status.INTERNAL_SERVER_ERROR, ex.getMessage());
        } 
        return result;
    }    

    @GET
    @Path("config")
    @Produces(MediaType.APPLICATION_JSON)
    public Config getConfig() {
        log.debug("Getting config");
        try {
            return new Config(burp.getConfig());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServerErrorException(Response.Status.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @PUT
    @Path("config")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setConfig(JAXBElement<Config> config) {
        log.debug("Setting config");
        burp.setConfig(config.getValue().entries);
        return Response.ok().entity(new JSONObject()).build();
    }

    @POST
    @Path("config")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateConfig(JAXBElement<Config> config) {
        log.debug("Updating config");
        burp.updateConfig(config.getValue().entries);
        return Response.ok().entity(new JSONObject()).build();
    }

    @GET
    @Path("proxy/history")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpMessageList getProxyHistory(@QueryParam("url") String url) {
        try {
            HttpMessageList historyList = new HttpMessageList();
            if (url == null) {
                historyList.messages = burp.getProxyHistory();
            } else {
                historyList.messages = burp.getProxyHistory(url);
            }
            return historyList;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServerErrorException(Response.Status.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @GET
    @Path("reset")
    @Produces(MediaType.APPLICATION_JSON)
    public Response clearScans() {
        try {
            burp.reset();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServerErrorException(Response.Status.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
        return Response.ok().build();
    }

    public void setBurpService(IBurpService service) {
        this.burp = service;
    }
}
