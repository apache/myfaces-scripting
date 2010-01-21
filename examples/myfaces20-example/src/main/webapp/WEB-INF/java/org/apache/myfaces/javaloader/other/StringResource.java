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
package org.apache.myfaces.javaloader.other;

import javax.faces.application.Resource;
import javax.faces.context.FacesContext;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.Map;
import java.net.URL;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class StringResource extends Resource {

    String resourceString = "";

    public StringResource(String resourceString) {
        this.resourceString = resourceString;
    }

    @Override
    public InputStream getInputStream() {

        return new ByteArrayInputStream(resourceString.getBytes());  
    }

    @Override
    public String getRequestPath() {
        return null;  
    }

    @Override
    public Map<String, String> getResponseHeaders() {
        return null;  
    }

    @Override
    public URL getURL() {
        return null;  
    }

    @Override
    public boolean userAgentNeedsUpdate(FacesContext context) {
        return false;  
    }
}
