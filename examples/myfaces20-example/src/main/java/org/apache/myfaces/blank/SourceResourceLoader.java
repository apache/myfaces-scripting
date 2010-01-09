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
package org.apache.myfaces.blank;

import org.apache.myfaces.resource.ExternalContextResourceLoader;
import org.apache.myfaces.scripting.core.util.WeavingContext;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * internal resource loader to be used with our custom resource handler
 * the resource loader is added to the list of available loaders
 * so that the resource gets loaded properly from our source path
 * instead of the web context if present, the source paths as usual
 * are picked up by our context params.
 */
class SourceResourceLoader extends ExternalContextResourceLoader {

    public SourceResourceLoader(String prefix) {
        super(prefix);
    }

    @Override
    protected Set<String> getResourcePaths(String path) {
        List<String> resourceRoots = WeavingContext.getConfiguration().getResourceDirs();
        if (resourceRoots == null || resourceRoots.isEmpty()) {
            return Collections.EMPTY_SET;
        }
        Set<String> retVals = new HashSet<String>(resourceRoots.size());
        for (String resourceRoot : resourceRoots) {
            retVals.add(resourceRoot + "/" + path);
        }
        return retVals;
    }
}
