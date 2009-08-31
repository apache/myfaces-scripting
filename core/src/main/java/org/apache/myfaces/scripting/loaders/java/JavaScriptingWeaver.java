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
package org.apache.myfaces.scripting.loaders.java;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.scripting.loaders.java.jsr199.CompilerFacade;
import org.apache.myfaces.scripting.api.DynamicCompiler;
import org.apache.myfaces.scripting.api.ScriptingConst;
import org.apache.myfaces.scripting.api.ScriptingWeaver;
import org.apache.myfaces.scripting.core.BaseWeaver;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

/**
 * @author werpu
 *         <p/>
 *         The Scripting Weaver for the java core which reloads the java scripts
 *         dynamically upon change
 */
public class JavaScriptingWeaver extends BaseWeaver implements ScriptingWeaver {

    Log log = LogFactory.getLog(JavaScriptingWeaver.class);
    String classPath = "";

    /**
     * helper to allow initial compiler classpath scanning
     *
     * @param servletContext
     */
    public JavaScriptingWeaver(ServletContext servletContext) {
        super(".java", ScriptingConst.ENGINE_TYPE_JAVA);
        initClasspath(servletContext);
    }


    private void initClasspath(ServletContext context) {
        String webInf = context.getRealPath(File.separator + "WEB-INF");
        StringBuilder classPath = new StringBuilder(255);
        File jarRoot = new File(webInf + File.separator + "lib");

        classPath.append(webInf);
        classPath.append(File.separator);
        classPath.append("classes");

        if (jarRoot.exists()) {
            log.info("Scanning paths for possible java compiler classpaths");
            //TODO make the scan recursive for directory trees
            String[] fileNames = jarRoot.list(new FilenameFilter() {
                public boolean accept(File dir,
                                      String name) {
                    name = name.toLowerCase();
                    name = name.trim();
                    return name.endsWith(".jar") || name.endsWith(".zip");
                }
            });

            for (String name : fileNames) {
                classPath.append(File.pathSeparator);
                classPath.append(webInf);
                classPath.append(File.separator);
                classPath.append("lib");
                classPath.append(File.separator);
                classPath.append(name);
            }

            this.classPath = classPath.toString();
            //TODO also go one level up to scan for the lib dir of the ear container
            //TODO add additional jar scan paths via configuration
            //for now this should do it
        } else {
            log.warn("web-inf/lib not found, you might have to adjust the jar scan paths manually");
        }

    }

    /**
     * helper to map the properties wherever possible
     *
     * @param target
     * @param src
     */
    protected void mapProperties(Object target, Object src) {
        try {
            BeanUtils.copyProperties(target, src);
        } catch (IllegalAccessException e) {
            log.debug(e);
            //this is wanted
        } catch (InvocationTargetException e) {
            log.debug(e);
            //this is wanted
        }
    }


  


    /**
     * loads a class from a given sourceroot and filename
     *
     * @param sourceRoot the source search lookup path
     * @param file       the filename to be compiled and loaded
     * @return a valid class if it could be found, null if none was found
     */
    @Override
    protected Class loadScriptingClassFromFile(String sourceRoot, String file) {
        //we load the scripting class from the given className
        File currentClassFile = new File(sourceRoot + File.separator + file);
        if (!currentClassFile.exists()) {
            return null;
        }

        if (log.isInfoEnabled()) {
            log.info("Loading Java file:" + file);
        }

        Iterator<String> it = scriptPaths.iterator();
        Class retVal = null;

        try {
            //we initialize the compiler lazy
            //because the facade itself is lazy
            DynamicCompiler compiler = new CompilerFacade();
            retVal = compiler.compileFile(sourceRoot, classPath, file);
        } catch (ClassNotFoundException e) {
            //can be safely ignored
        }

        if (retVal != null) {
            refreshReloadingMetaData(sourceRoot, file, currentClassFile, retVal, ScriptingConst.ENGINE_TYPE_JAVA);
        }

        return retVal;
    }

}
