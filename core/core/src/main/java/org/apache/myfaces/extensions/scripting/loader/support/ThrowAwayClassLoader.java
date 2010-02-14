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
package org.apache.myfaces.extensions.scripting.loader.support;

/**
 * <p>A class loader that implements this interface is able to throw away class definitions.
 * Well, to be more precise, no class loader is really able to do that, but you can get this
 * behaviour by just throwing away this class loader. Note that a class loader implementing
 * this interface is supposed to load just a single class definition, i.e. there's a 1:1
 * relationship between the class loader and the class. In doing so, we're somehow able to
 * throw away class definitions and replace them with newer versions.</p>
 */
public interface ThrowAwayClassLoader {

    /**
     * <p>Loads the class with the specified class name. However, note that implementing
     * classes are just supposed to load a single class, so if you want to load a different
     * class than that, this class loader will just delegate to the parent class loader.</p>
     *
     * @param className the name of the class you want to load
     * @param resolve   if <tt>true</tt> then resolve the class
     * @return the resulting Class reference
     * @throws ClassNotFoundException if the class could not be found
     */
    public Class loadClass(String className, boolean resolve) throws ClassNotFoundException;

    /**
     * <p>Returns <code>true</code> if the given "last modified"-timestamp is
     * more recent than the timestamp of this ClassLoader, i.e. if this ClassLoader
     * is to be destroyed as there is a newer class file available.
     *
     * @param lastModified the "last modified"-timestamp of the class file you want to load
     * @return <code>true</code> if the given "last modified"-timestamp is
     *         more recent than the timestamp of this class loader
     */
    public boolean isOutdated(long lastModified);

}