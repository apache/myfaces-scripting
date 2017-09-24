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

package org.apache.myfaces.extensions.scripting.spring.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URLConnection;

/**
 * <p>Utility methods for dealing with resource files in the
 * file system. Mainly for internal use within the module.</p>
 */
public class ResourceUtils {

    /**
     * The logger instance for this class.
     */
    private static final Log logger = LogFactory.getLog(ResourceUtils.class);

    // ------------------------------------------ Public static methods

    /**
     * <p>Determines whether the given resource file is already outdated compared
     * to the given timestamp, i.e. it compares the given timestamp to the last
     * modified timestamp of the given resource and returns <code>true</code>
     * if the given timestamp is less than the timestamp of the given resource.</p>
     * <p>&nbsp;</p>
     * <p>Note that this methods assumes that the given resource exists, otherwise
     * an exception will be thrown.</p>
     *
     * @param timestamp the timestamp that you want to compare the resource to
     * @param resource  the resource that you want to check
     * @return <code>true</code>, if the given resource is outdated, otherwise
     *         <code>false</code>
     * @throws java.io.IOException if an I/O error occurs
     */
    public static boolean isOutdated(long timestamp, Resource resource) throws IOException {
        long lastModified;

        if (resource instanceof FileSystemResource) {
            lastModified = resource.getFile().lastModified();
        }
        else {
            URLConnection connection = resource.getURL().openConnection();
            connection.setUseCaches(false);

            // The problem with JarURLConnections is that they seemingly don't care
            // much about the last modified timestamp, they always return 0.
            if (connection instanceof JarURLConnection) {
                connection = ((JarURLConnection) connection).getJarFileURL().openConnection();
                connection.setUseCaches(false);
            }

            try {
                lastModified = connection.getLastModified();
            } finally {
                try {
                    connection.getInputStream().close();
                } catch (IOException ex) {
                    if (logger.isErrorEnabled()) {
                        logger.error("An I/O error occured while closing the URL connection for the resource '" + resource + "'.", ex);
                    }
                }
            }
        }

        return timestamp < lastModified;
    }

}
