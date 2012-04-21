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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import net.continuumsecurity.restyburp.BurpService;

import org.apache.log4j.Logger;

/**
 *
 * @author stephen
 */
 
@XmlRootElement
public class HttpMessageList {
    static Logger log = Logger.getLogger(HttpMessageList.class.toString());

    @XmlElementWrapper(name = "message")
    public List<HttpMessage> messages;
    
    public void setMessages(List<HttpMessage> messages) {
    	this.messages = messages;
    }

    public List<HttpMessage> findInMessages(String regex, MessageType type) {
		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE
				| Pattern.DOTALL | Pattern.MULTILINE);
		List<HttpMessage> result = new ArrayList<HttpMessage>();
		for (int i = messages.size() - 1; i >= 0; i--) {
			String stringBean = "";
			HttpMessage bean = messages.get(i);

			try {
				// TODO read encoding from headers
				// TODO Bug here, for some long pages (>40 000 BYTES) the
				// matcher hangs.
				if (type == MessageType.REQUEST && bean.getRequest() != null) {
					stringBean = new String(bean.getRequest(), "UTF8");
				}
				if (type == MessageType.RESPONSE && bean.getResponse() != null) {
					stringBean = new String(bean.getResponse(), "UTF8");
				}
				log.debug("Searching in message to: " + bean.getUrl());
				Matcher m = p.matcher(stringBean);
				log.trace("Searching in message: " + bean.getUrl());
				if (m.find()) {
					log.trace("Found regex: " + regex);
					result.add(bean);
				} else {
					log.trace("Did not find regex: " + regex);
				}
			} catch (UnsupportedEncodingException e) {
				log.error(e.getMessage());
				e.printStackTrace();
			}
		}

		return result;
	}

}
