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
package org.apache.myfaces.extensions.scripting.cdi.monitor;

import org.apache.myfaces.extensions.scripting.cdi.monitor.resources.Resource;
import org.apache.myfaces.extensions.scripting.cdi.monitor.resources.ResourceMonitor;
import org.apache.myfaces.extensions.scripting.cdi.monitor.resources.ResourceResolver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 
 *
 */
public class FileMonitoringTask implements Runnable {

    /**
     * Contains the timestamps of previously found resources.
     */
    private final Map<Resource, Long> timestamps = new HashMap<Resource, Long>();

    /**
     * Contains the resource monitors along with the according resource resolvers
     * identifying the set of resources that those monitors are monitoring.
     */
    private final Map<ResourceResolver, ResourceMonitor> monitors =
                        new HashMap<ResourceResolver, ResourceMonitor>();

    // ------------------------------------------ Public methods

    public void registerResourceMonitor(ResourceResolver resolver, ResourceMonitor monitor) {
        if (resolver == null || monitor == null) {
            throw new IllegalArgumentException(
                "Neither the given resolver nor the given monitor must be null, at least one of both was though.");
        }

        synchronized (this.monitors) {
            monitors.put(resolver, monitor);
        }
    }

    // ------------------------------------------ Runnable methods
    
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            Iterator<Map.Entry<ResourceResolver, ResourceMonitor>> monitors;
            synchronized (this.monitors) {
                monitors = new HashMap<ResourceResolver, ResourceMonitor>(this.monitors).entrySet().iterator();
            }

            while (monitors.hasNext()) {
                final Map.Entry<ResourceResolver, ResourceMonitor> entry = monitors.next();

                ResourceResolver resourceResolver = entry.getKey();
                resourceResolver.resolveResources(
                    new ResourceResolver.ResourceCallback() {
                        public boolean handle(Resource resource) {
                            // If we're either dealing with a file that we haven't seen so far or we're
                            // dealing with an updated version of a file that we've already seen ..
                            Long lastModified = timestamps.get(resource);
                            if ((lastModified == null)
                                    || (lastModified != null && lastModified < resource.lastModified())) {
                                timestamps.put(resource, resource.lastModified());

                                // .. notify the monitor about it and tell the calling method
                                // whether the given monitor wants to continue the scan.
                                ResourceMonitor monitor = entry.getValue();
                                return monitor.resourceModified(resource);
                            }

                            // If it's any other file (i.e. a file that hasn't been modified), continue searching.
                            return true;
                        }
                    }
                );
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

}