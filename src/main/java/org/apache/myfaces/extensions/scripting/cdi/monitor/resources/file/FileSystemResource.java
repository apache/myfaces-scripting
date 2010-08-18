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
package org.apache.myfaces.extensions.scripting.cdi.monitor.resources.file;

import org.apache.myfaces.extensions.scripting.cdi.monitor.resources.Resource;

import java.io.File;

/**
 * 
 */
public class FileSystemResource implements Resource {

    private File file;

    // ------------------------------------------ Constructors

    public FileSystemResource(File file) {
        if (file == null) {
            throw new IllegalArgumentException(
                    "The given file must not be null.");
        }

        this.file = file;
    }

    // ------------------------------------------ Resource methods

    /**
     * <p>Returns a reference to this resource on the file system,
     * i.e it returns a reference to a java.io.File object.</p>
     *
     * @return a reference to this resource on the file system
     */
    public File getFile() {
        return file;
    }

    /**
     * <p>Returns the time that the resource denoted by this reference was last modified.</p>
     *
     * @return A <code>long</code> value representing the time the file was
     *         last modified or <code>0L</code> if the file does not exist
     *         or if an I/O error occurs
     */
    public long lastModified() {
        return getFile().lastModified();
    }

    // ------------------------------------------ Object methods


    /**
     * <p>Returns a hash code value for the object. This method is
     * supported for the benefit of hash tables such as those provided by
     * <code>java.util.Hashtable</code>.</p>
     *
     * @return a hash code value for this object
     *
     * @see java.io.File#hashCode()
     * @see java.util.Hashtable
     */
    @Override
    public int hashCode() {
        return getFile().hashCode();
    }

    /**
     * <p>Indicates whether some other object is "equal to" this one.</p>
     *
     * @param obj the reference object with which to compare.
     * @return <code>true</code> if this object is the same as the obj
     *         argument; <code>false</code> otherwise.
     * @see #hashCode()
     * @see java.util.Hashtable
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FileSystemResource) {
            FileSystemResource resource = (FileSystemResource) obj;
            return getFile().equals(resource.getFile());
        } else {
            return false;
        }
    }

    /**
     * <p>Returns a string representation of the object.</p>
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return String.format("FileSystemResource[file: '%s']", file);
    }
}