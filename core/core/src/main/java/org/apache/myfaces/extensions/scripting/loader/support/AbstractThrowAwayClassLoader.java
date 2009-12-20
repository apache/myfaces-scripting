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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

/**
 *
 */
public abstract class AbstractThrowAwayClassLoader extends URLClassLoader
        implements ThrowAwayClassLoader {

    /**
     * The size of the buffer we're going to use to copy the contents from a stream to a byte array.
     */
    private static final int BUFFER_SIZE = 4096;

    /**
     * Indicates when this ClassLoader has been created.
     */
    private final long timestamp;

    /**
     * The name of the class that this class loader is going to load.
     */
    private final String className;

    // ------------------------------------------ Constructors

    public AbstractThrowAwayClassLoader(String className, ClassLoader parentClassLoader) {
        super(new URL[0], parentClassLoader);

        if (className == null) {
            throw new IllegalArgumentException("The given class name must not be null.");
        }

        // Save a timestamp of the time this class loader has been created. In doing
        // so, we're able to tell if this class loader is already outdated or not.
        this.timestamp = System.currentTimeMillis();
        this.className = className;
    }

    // ------------------------------------------ ThrowAwayClassLoader methods

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
    public Class loadClass(String className, boolean resolve) throws ClassNotFoundException {
        Class c;

        // Note that this class loader is only supposed to load a specific Class reference,
        // hence the check against the class name. Otherwise this class loader would try to
        // resolve class files for dependent classes as well, which means that there would
        // be different versions of the same Class reference in the system.
        if (isEligibleForLoading(className)) {
            // First, check if the class has already been loaded
            c = findLoadedClass(className);
            if (c == null) {
                // Note that execution reaches this point only if we're either updating a
                // dynamically loaded class or loading it for the first time. Otherwise
                // this ClassLoader would have returned an already loaded class (see the
                // call to findLoadedClass()).
                c = findClass(className);
                if (resolve) {
                    resolveClass(c);
                }
            }
        }

        // If this class loader isn't supposed to load the given class it doesn't
        // necessarily mean, that we're not dealing with a dynamic class here.
        // However, if that's the case, we really want to use the same class loader
        // (i.e. the same ClassFileLoader instance) as Spring does, hence the
        // delegation to the parent class loader (i.e. the ReloadingClassLoader
        // again).
        else {
            c = super.loadClass(className, resolve);
        }

        return c;
    }

    /**
     * <p>Returns <code>true</code> if the given "last modified"-timestamp is
     * more recent than the time stamp of this class loader, i.e. if this class loader
     * is to be destroyed as there is a newer class file available.
     *
     * @param lastModified the "last modified"-timestamp of the class file you want to load
     * @return <code>true</code> if the given "last modified"-timestamp is
     *         more recent than the time stamp of this ClassLoader
     */
    public boolean isOutdated(long lastModified) {
        return timestamp < lastModified;
    }

    // ------------------------------------------ Utility methods

    /**
     * <p>Determines whether this class loader is supposed to load the given class.</p>
     * 
     * @param className the name of the class
     * 
     * @return <code>true</code>, if this class loader is supposed to load the
     *          given class, <code>false</code> otherwise
     */
    protected boolean isEligibleForLoading(String className) {
        return getClassName().equals(className);
    }

    /**
     * <p>Finds and loads the class with the specified name from the compilation path.</p>
     *
     * @param className the name of the class
     * @return the resulting class
     * @throws ClassNotFoundException if the class could not be found
     */
    protected Class findClass(final String className) throws ClassNotFoundException {
        if (isEligibleForLoading(className)) {
            try {
                return AccessController.doPrivileged(new PrivilegedExceptionAction<Class<?>>() {
                    public Class<?> run() throws Exception {
                        InputStream stream = null;

                        try {
                            // Load the raw bytes of the class file into the memory ..
                            stream = openStreamForClass(className);
                            if (stream != null) {
                                byte[] buffer = loadClassFromStream(stream);

                                // .. and create an according Class object.
                                return defineClass(className, buffer, 0, buffer.length);
                            } else {
                                throw new ClassNotFoundException(
                                        "Cannot find the resource that defines the class '" + className + "'.");
                            }
                        }
                        catch (IOException ex) {
                            throw new ClassNotFoundException(
                                    "Cannot load the raw byte contents for the class '" + className + "'.", ex);
                        }
                        finally {
                            if (stream != null) {
                                stream.close();
                            }
                        }
                    }
                });
            }
            catch (PrivilegedActionException e) {
                throw (ClassNotFoundException) e.getException();
            }
        } else {
            throw new ClassNotFoundException(
                    "This class loader only knows how to load the class '" + getClassName() + "'.");
        }
    }

    /**
     * <p>Returns the name of the class that this class loader is going to load.</p>
     *
     * @return the name of the class that this class loader is going to load
     */
    protected String getClassName() {
        return className;
    }

    /**
     * <p>Loads the byte array that you can use to define the given class
     * afterwards using a call to {@link #defineClass}.</p>
     *
     * @param stream a stream referencing e.g. a .class file
     * @return the byte array that you can use to define the given class
     * @throws IOException if an I/O error occurs
     */
    private byte[] loadClassFromStream(InputStream stream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream(BUFFER_SIZE * 5);

        byte[] buffer = new byte[BUFFER_SIZE];

        int readBytes;
        while ((readBytes = stream.read(buffer)) != -1) {
            result.write(buffer, 0, readBytes);
        }

        return result.toByteArray();
    }

    // ------------------------------------------ Abstract methods

    /**
     * <p>Opens a stream to the resource that defines the given class. If it
     * cannot be found, return <code>null</code>.</p>
     *
     * @param className the class to load
     * @return a stream to the resource that defines the given class
     * @throws IOException if an I/O error occurs
     */
    protected abstract InputStream openStreamForClass(String className) throws IOException;

}
