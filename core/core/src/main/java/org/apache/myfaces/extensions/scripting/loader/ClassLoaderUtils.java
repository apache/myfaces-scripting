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
package org.apache.myfaces.extensions.scripting.loader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>Utility class for class loading purposes, e.g. to determine the classpath of a
 * class loader hierarchy.</p>
 *
 * @author Bernhard Huemer
 */
public class ClassLoaderUtils {

    /**
     * The logger instance for this class.
     */
    private static final Log logger = LogFactory.getLog(ClassLoaderUtils.class);

    // ------------------------------------------ Public methods

    /**
     * <p>Returns the default class loader to use.</p>
     *
     * @return the default class loader to use
     */
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader != null) {
            return classLoader;
        } else {
            return ClassLoaderUtils.class.getClassLoader();
        }
    }

    /**
     * <p>Determines whether the given class is loadable by the given class loader.</p>
     *
     * @param className   the class you want to check
     * @param classLoader the class loader to use for that check
     * @return <code>true</code>, if the given class is loadable by the given class loader
     */
    public static boolean isClassAvailable(String className, ClassLoader classLoader) {
        try {
            classLoader.loadClass(className);
            return true;
        }
        catch (Throwable ex) {
            return false;
        }
    }

    /**
     * <p>Resolves the classpath by walking up the hierachy of class loaders. Assuming
     * that we're only dealing with URLClassLoaders it's possible to determine the
     * classpath. This method, however, returns the classpath as a String, where each
     * classpath entry is separated by a ';', i.e. it returns the classpath in a format
     * that Java tools usually expect it to be.</p>
     *
     * @param classLoader the class loader which you want to resolve the class path for
     * @return the final classpath
     */
    public static String buildClasspath(ClassLoader classLoader) {
        StringBuffer classpath = new StringBuffer();

        URL[] urls = resolveClasspath(classLoader);
        for (URL url : urls) {
            classpath.append(url.getPath());

            // Note that the classpath separator character is platform
            // dependent. On Windows systems it's ";" whereas on other
            // UNIX systems it's ":".
            classpath.append(File.pathSeparatorChar);
        }

        return classpath.toString();
    }

    /**
     * <p>Resolves the classpath by walking up the hierarchy of class loaders. Assuming
     * that we're only dealing with URLClassLoaders it's possible to determine the
     * classpath.</p>
     *
     * @param parent the class loader which you want to resolve the class path for
     * @return the final classpath
     */
    public static URL[] resolveClasspath(ClassLoader parent) {
        List<URL> classpath = new ArrayList<URL>();

        ClassLoader classLoader = parent;
        // Walk up the hierarchy of class loaders in order to determine the current classpath.
        while (classLoader != null) {
            if (classLoader instanceof URLClassLoader) {
                URLClassLoader urlClassLoader = (URLClassLoader) classLoader;

                URL[] urls = urlClassLoader.getURLs();
                if (urls != null) {
                    classpath.addAll(Arrays.asList(urls));
                }
            } else {
                if (logger.isWarnEnabled()) {
                    logger.warn("Resolving the classpath of the classloader '" + parent + "' - One of its parent class"
                            + " loaders is no URLClassLoader '" + classLoader + "', which means it's possible that"
                            + " some classpath entries aren't in the final outcome of this method call.");
                }
            }

            // Inspect the parent class loader next.
            classLoader = classLoader.getParent();
        }

        return classpath.toArray(new URL[classpath.size()]);
    }

}
