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
package org.apache.myfaces.extensions.scripting.core.common.util;


import org.apache.myfaces.extensions.scripting.core.api.ClassLoaderService;
import org.apache.myfaces.extensions.scripting.core.api.WeavingContext;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>Utility class for class loading purposes, e.g. to determine the classpath of a
 * class loader hierarchy.</p>
 */
public class ClassLoaderUtils {

    /**
     * fetches a default classloader service which is responsible of registering
     * a classloader into the system
     *
     * @return  the classloading registration service
     */
    public static ClassLoaderService getDefaultClassLoaderService() {
        Iterator<ClassLoaderService>  serviceIt = ServiceLoader.load(ClassLoaderService.class).iterator();
        ClassLoaderService finalService = null;
        while(serviceIt.hasNext()) {
            ClassLoaderService service = serviceIt.next();
            if(finalService == null || service.getPriority() > finalService.getPriority()) {
                finalService = service;
            }
        }
        return finalService;
    }

    // ------------------------------------------ Public methods

    /**
     * CompilationResult
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
     * <p>&nbsp;</p>
     * it also adds the additional classpaths issued by our configuration to the list
     *
     * @param classLoader the class loader which you want to resolve the class path for
     * @return the final classpath
     */
    public static String buildClasspath(ClassLoader classLoader) {
        StringBuilder classpath = new StringBuilder();

        URL[] urls = resolveClasspath(classLoader);
        for (URL url : urls) {
            try
            {
                classpath.append(URLDecoder.decode(url.getPath(), Charset.defaultCharset().toString()));
                // Note that the classpath separator character is platform
                // dependent. On Windows systems it's ";" whereas on other
                // UNIX systems it's ":".
                classpath.append(File.pathSeparatorChar);
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        String retVal = classpath.toString();
        if (retVal.endsWith(File.pathSeparator)) {
            retVal = retVal.substring(0, retVal.length() - 1);
        }
        return retVal;
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
        // Walk up the hierachy of class loaders in order to determine the current classpath.
        File target = WeavingContext.getInstance().getConfiguration().getCompileTarget();
        if (target != null) {
            addFile(classpath, target);
        }

        while (classLoader != null) {
            if (classLoader instanceof URLClassLoader) {
                URLClassLoader urlClassLoader = (URLClassLoader) classLoader;

                URL[] urls = urlClassLoader.getURLs();
                if (urls != null) {
                    classpath.addAll(Arrays.asList(urls));
                }
            } /*else {
                if (logger.isWarnEnabled()) {
                    logger.warn("Resolving the classpath of the classloader '" + parent + "' - One of its parent class"
                            + " loaders is no URLClassLoader '" + classLoader + "', which means it's possible that"
                            + " some classpath entries aren't in the final outcome of this method call.");
                }
            } */

            //we disable this warning entirely for now because our own url classloader
            //can deal with this properly, due to extra startup context classpath determination

            // Inspect the parent class loader next.
            classLoader = classLoader.getParent();
        }

        List<String> additionalClassPaths = WeavingContext.getInstance().getConfiguration().getAdditionalClassPath();
        if (!(additionalClassPaths == null || additionalClassPaths.isEmpty())) {
            for (String additionalClassPath : additionalClassPaths) {
                File additionalPath = new File(additionalClassPath);
                addFile(classpath, additionalPath);
            }
        }

        return classpath.toArray(new URL[classpath.size()]);
    }

    private static void addFile(List<URL> classpath, File additionalPath) {
        if (additionalPath.exists()) {
            try {
                classpath.add(additionalPath.toURI().toURL());
            } catch (MalformedURLException e) {
                Logger log = Logger.getLogger(ClassLoaderUtils.class.getName());
                log.log(Level.SEVERE, "Additionalclasspath wrong url", e);
            }
        }
    }

}
