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

import java.net.URL;

/**
 *
 * @author stephen
 */
public class Utils {
    public static synchronized boolean containsUrl(URL first, URL second) {
        if (!first.getHost().equals(second.getHost())) return false;
        if (first.getPort() != (second.getPort())) return false;
        if (!first.getProtocol().equals(second.getProtocol())) return false;
        if (!first.getPath().startsWith(second.getPath())) return false;
        return true;
    }
}
