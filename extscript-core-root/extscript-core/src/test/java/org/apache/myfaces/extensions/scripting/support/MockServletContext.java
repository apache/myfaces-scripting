/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.myfaces.extensions.scripting.support;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Basic unit testing servlet context mock
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class MockServletContext implements ServletContext {

    Map<String, Object> _attributes = new HashMap<String, Object>();
    Map<String, String> _initParameters = new HashMap<String, String>();
    String _resourceRoot = "../../src/test/resources/webapp";

    public void addInitParameter(String key, String value) {
        _initParameters.put(key, value);
    }

    public ServletContext getContext(String s) {
        return this;
    }

    public int getMajorVersion() {
        throw new UnsupportedOperationException("getServletContextName()");
    }

    public int getMinorVersion() {
        throw new UnsupportedOperationException("getServletContextName()");
    }

    public String getMimeType(String s) {
        throw new UnsupportedOperationException("getServletContextName()");
    }

    public Set getResourcePaths(String s) {
        HashSet retVal = new HashSet();
        retVal.add(_resourceRoot);
        return retVal;
    }

    public URL getResource(String s) throws MalformedURLException {
        return (new File(getRealPath(s))).toURI().toURL();
    }

    public InputStream getResourceAsStream(String s) {
        throw new UnsupportedOperationException("getServletContextName()");
    }

    public RequestDispatcher getRequestDispatcher(String s) {
        throw new UnsupportedOperationException("getServletContextName()");
    }

    public RequestDispatcher getNamedDispatcher(String s) {
        throw new UnsupportedOperationException("getServletContextName()");
    }

    public Servlet getServlet(String s) throws ServletException {
        throw new UnsupportedOperationException("getServletContextName()");
    }

    public Enumeration getServlets() {
        throw new UnsupportedOperationException("getServletContextName()");
    }

    public Enumeration getServletNames() {
        throw new UnsupportedOperationException("getServletContextName()");
    }

    public void log(String s) {
        throw new UnsupportedOperationException("getServletContextName()");
    }

    public void log(Exception e, String s) {
        throw new UnsupportedOperationException("getServletContextName()");
    }

    public void log(String s, Throwable throwable) {
        throw new UnsupportedOperationException("getServletContextName()");
    }

    public String getRealPath(String s) {
        if (s.startsWith("/") || s.startsWith("\\")) {
            s = s.substring(1);
        }

        return Thread.currentThread().getContextClassLoader().getResource("./").getPath() + _resourceRoot + File.separator + s;
    }

    public String getServerInfo() {
        throw new UnsupportedOperationException("getServletContextName()");
    }

    public String getInitParameter(String s) {
        return _initParameters.get(s);
    }

    public Enumeration getInitParameterNames() {
        throw new UnsupportedOperationException("getServletContextName()");
    }

    public Object getAttribute(String s) {
        return _attributes.get(s);
    }

    public Enumeration getAttributeNames() {
        throw new UnsupportedOperationException("getServletContextName()");
    }

    public void setAttribute(String s, Object o) {
        _attributes.put(s, o);
    }

    public void removeAttribute(String s) {
        _attributes.remove(s);
    }

    public String getServletContextName() {
        throw new UnsupportedOperationException("getServletContextName()");
    }
}
