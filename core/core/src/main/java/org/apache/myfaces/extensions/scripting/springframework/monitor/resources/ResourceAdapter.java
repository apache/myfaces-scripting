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
package org.apache.myfaces.extensions.scripting.springframework.monitor.resources;

import org.apache.myfaces.extensions.scripting.monitor.resources.Resource;

import java.io.File;
import java.io.IOException;

/**
 * <p>A resource implementation that basically just adapts Spring resources such that
 * you can use Spring resource objects within MyFaces Scripting as well. In doing so,
 * you're able to use its pattern based resource resolving mechanisms as well.</p>
 *
 * @author Bernhard Huemer
 */
public class ResourceAdapter implements Resource {

    /** The underlying Spring resource object that we're delegating to. */
    private org.springframework.core.io.Resource resource;

    // ------------------------------------------ Constructors

    /**
     * <p>Constructs a new resource adapter using the underlying Spring resource object
     * that this adapter is delegating to afterwards.</p>
     *
     * @param resource the underlying Spring resource object
     */
    public ResourceAdapter(org.springframework.core.io.Resource resource) {
        if (resource == null) {
            throw new IllegalArgumentException(
                    "The given resource object must not be null.");
        }

        this.resource = resource;
    }

    // ------------------------------------------ Resource methods

    /**
     * <p>Returns a reference to this resource on the file system,
     * i.e it returns a reference to a java.io.File object.</p>
     *
     * @return a reference to this resource on the file system
     */
    public File getFile() {
        try {
            return resource.getFile();
        } catch (IOException ex) {
            // This shouldn't happen as we're actually only dealing with Spring resource
            // implementations that don't throw this exception, hence we rethrow this
            // exception unchecked.
            throw new IllegalStateException(
                    "The Spring resource object isn't supposed to throw an IOException!", ex);
        }
    }

    /**
     * <p>Returns the time that the resource denoted by this reference was last modified.</p>
     *
     * @return A <code>long</code> value representing the time the file was
     *         last modified or <code>0L</code> if the file does not exist
     *         or if an I/O error occurs
     */
    public long lastModified() {
        try {
            return resource.lastModified();
        } catch (IOException ex) {
            return 0L;
        }
    }
    
}
