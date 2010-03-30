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

package org.apache.myfaces.scripting.core.support;

import java.io.File;

/**
 * Supportive utils to access the source
 * probes directly
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class PathUtils {

    String _currentPath;
    String _resourceRoot;

    public PathUtils() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        //we use a location relative to our current root one to reach the sources
        //because the test also has to be performed outside of maven
        //and the ide cannot cope with resource paths for now
        _currentPath = loader.getResource("./").getPath();
        _resourceRoot = _currentPath + "../../src/test/resources";
    }

    /**
     * Resource root dir getter
     *
     * @return the resource root dir (from our source package)
     */
    public String getResourceRoot() {
        return _resourceRoot;
    }

    public String getResource(String in) {
        if(in.startsWith("//") || in.startsWith("\\")) {
            in = in.substring(1);
        }
        return _resourceRoot + File.separator + in;
    }

}
