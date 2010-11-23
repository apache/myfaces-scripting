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
package org.apache.myfaces.extensions.scripting.facelet;

import com.sun.facelets.impl.DefaultResourceResolver;
import org.apache.myfaces.extensions.scripting.core.util.WeavingContext;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * decorated Facelet resource resolver to reroute
 * the resource requests to our source path if possible
 */
public class ReroutingResourceResolver extends DefaultResourceResolver {

    DefaultResourceResolver _delegate = new DefaultResourceResolver();
    volatile boolean _initiated = false;
    List<String> _resourceDirs = null;

    Logger log = Logger.getLogger(this.getClass().getName());

    @Override
    public URL resolveUrl(String path) {

        if (!_initiated) {
            _resourceDirs = WeavingContext.getConfiguration().getResourceDirs();
            _initiated = true;
        }

        if (_resourceDirs != null && !_resourceDirs.isEmpty()) {
            for (String resourceDir : _resourceDirs) {
                File resource = new File(resourceDir + path);
                if (resource.exists()) try {
                    return resource.toURI().toURL();
                } catch (MalformedURLException e) {
                    log.log(Level.SEVERE,"", e);
                }
            }
        }

        return _delegate.resolveUrl(path);
    }
}

