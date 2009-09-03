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
import org.apache.myfaces.scripting.api.BaseWeaver;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

/**
 * @author werpu
 *         <p/>
 *         The Scripting Weaver for the java core which reloads the java scripts
 *         dynamically upon change
 */
public class JavaScriptingWeaver extends BaseWeaver implements ScriptingWeaver {

    Log log = LogFactory.getLog(JavaScriptingWeaver.class);
    String classPath = "";
    DynamicClassIdentifier identifier = new DynamicClassIdentifier();

    /**
     * this override is needed because we cannot sanely determine all jar
     * paths we need for our compiler in the various web container configurations
     */
    static final String CUSTOM_JAR_PATHS = "org.apache.myfaces.scripting.java.JAR_PATHS";
    /*comma separated list of additional classpaths*/
    static final String CUSTOM_CLASS_PATHS = "org.apache.myfaces.scripting.java.CLASS_PATHS";

    private static final String JAVA_FILE_ENDING = ".java";

    /**
     * helper to allow initial compiler classpath scanning
     *
     * @param servletContext
     */
    public JavaScriptingWeaver(ServletContext servletContext) {
        super(JAVA_FILE_ENDING, ScriptingConst.ENGINE_TYPE_JAVA);
        initClasspath(servletContext);
    }

    public JavaScriptingWeaver() {
        super(JAVA_FILE_ENDING, ScriptingConst.ENGINE_TYPE_JAVA);
    }


    /**
     * recursive directory scan
     *
     * @param rootPath
     * @return
     */
    private List<String> scanPath(String rootPath) {
        File jarRoot = new File(rootPath);

        List<String> retVal = new LinkedList<String>();
        String[] dirs = jarRoot.list(new FilenameFilter() {
            public boolean accept(File dir,
                                  String name) {

                String dirPath = dir.getAbsolutePath();
                File checkFile = new File(dirPath + File.separator + name);
                return checkFile.isDirectory() && !(name.equals(".") && !name.equals(".."));
            }
        });

        for (String dir : dirs) {
            retVal.addAll(scanPath(rootPath + File.separator + dir));
        }

        String[] foundNames = jarRoot.list(new FilenameFilter() {
            public boolean accept(File dir,
                                  String name) {

                name = name.toLowerCase();
                name = name.trim();
                String dirPath = dir.getAbsolutePath();
                File checkFile = new File(dirPath + File.separator + name);
                return (!checkFile.isDirectory()) && name.endsWith(".jar") || name.endsWith(".zip");
            }
        });

        for (String foundPath : foundNames) {
            retVal.add(rootPath + File.separator + foundPath);
        }
        return retVal;
    }

    private void initClasspath(ServletContext context) {
        String webInf = context.getRealPath(File.separator + "WEB-INF");
        StringBuilder classPath = new StringBuilder(255);
        File jarRoot = new File(webInf + File.separator + "lib");

        classPath.append(webInf);
        classPath.append(File.separator);
        classPath.append("classes");

        List<String> fileNames = new LinkedList<String>();
        if (jarRoot.exists()) {
            log.info("Scanning paths for possible java compiler classpaths");

            this.classPath = classPath.toString() + File.pathSeparatorChar + addExternalClassPaths(context) + File.pathSeparator + addStandardJarPaths(jarRoot) + addExternalJarPaths(context);

        } else {
            log.warn("web-inf/lib not found, you might have to adjust the jar scan paths manually");
        }
    }

    private String addStandardJarPaths(File jarRoot) {
        List<String> fileNames = new LinkedList<String>();
        StringBuilder retVal = new StringBuilder();
        fileNames.addAll(scanPath(jarRoot.getAbsolutePath()));
        int cnt = 0;
        for (String classPath : fileNames) {
            cnt++;
            retVal.append(classPath);
            if (cnt < fileNames.size()) {
                retVal.append(File.pathSeparator);
            }
        }
        return retVal.toString();
    }

    private String addExternalClassPaths(ServletContext context) {
        String classPaths = context.getInitParameter(CUSTOM_CLASS_PATHS);
        if (classPaths != null && !classPaths.trim().equals("")) {
            String[] classPathArr = classPaths.split(",");
            StringBuilder retVal = new StringBuilder();
            int cnt = 0;
            for (String classPath : classPathArr) {
                cnt++;
                retVal.append(classPath);
                if (cnt < classPathArr.length) {
                    retVal.append(File.pathSeparator);
                }
            }
            return retVal.toString();
        }
        return "";
    }


    private String addExternalJarPaths(ServletContext context) {
        List<String> fileNames = new LinkedList<String>();
        String jarPaths = context.getInitParameter(CUSTOM_JAR_PATHS);
        StringBuilder retVal = new StringBuilder();
        if (jarPaths != null && !jarPaths.trim().equals("")) {
            String[] jarPathsArr = jarPaths.split(",");
            for (String jarPath : jarPathsArr) {
                fileNames.addAll(scanPath(jarPath));
            }
        }
        int cnt = 0;
        for (String classPath : fileNames) {
            cnt++;
            retVal.append(classPath);
            if (cnt < fileNames.size()) {
                retVal.append(File.pathSeparator);
            }
        }
        return retVal.toString();
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

    public boolean isDynamic(Class clazz) {
        return identifier.isDynamic(clazz);  //To change body of implemented methods use File | Settings | File Templates.
    }
}
