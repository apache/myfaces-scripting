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
package org.apache.myfaces.javaloader.core;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.javaloader.core.jsr199.CompilerFacade;
import org.apache.myfaces.scripting.api.DynamicCompiler;
import org.apache.myfaces.scripting.api.ScriptingConst;
import org.apache.myfaces.scripting.api.ScriptingWeaver;
import org.apache.myfaces.scripting.refresh.FileChangedDaemon;
import org.apache.myfaces.scripting.refresh.ReloadingMetadata;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author werpu
 *         <p/>
 *         The Scripting Weaver for the java core which reloads the java scripts
 *         dynamically upon change
 */
public class JavaScriptingWeaver implements ScriptingWeaver {

    /**
     * only be set from the
     * initialisation code so no thread safety needed
     */
    List<String> scriptPaths = new LinkedList<String>();
    Log log = LogFactory.getLog(JavaScriptingWeaver.class);
    String classPath = "";

    /**
         * helper to allow initial compiler classpath scanning
         * @param servletContext
         */
    public JavaScriptingWeaver(ServletContext servletContext) {
        super();
        scanClasspath(servletContext);
        //TODO move the directory scannint for the sources also in here
    }

    private void scanClasspath(ServletContext context) {
        String webInf = context.getRealPath(File.separator+"WEB-INF");    
        StringBuilder classPath = new StringBuilder(255);
        File jarRoot = new File(webInf+File.separator+"lib");

        classPath.append(webInf);
        classPath.append(File.separator);
        classPath.append("classes");
        
        if(jarRoot.exists()) {
            log.info("Scanning paths for possible java compiler classpaths");
            String [] fileNames = jarRoot.list(new FilenameFilter() {
                public boolean accept(File dir,
                      String name) {
                      name = name.toLowerCase();
                      name = name.trim();
                      return name.endsWith(".jar") || name.endsWith(".zip");
                }
            });

            for(String name: fileNames) {
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
        }

    }

    /**
     * add custom source lookup paths
     *
     * @param scriptPath the new path which has to be added
     */
    public void appendCustomScriptPath(String scriptPath) {
        if (scriptPath.endsWith("/") || scriptPath.endsWith("\\/")) {
            scriptPath = scriptPath.substring(0, scriptPath.length() - 1);
        }
       
        scriptPaths.add(scriptPath);
    }

    /**
     * reloads a scripting instance object
     *
     * @param scriptingInstance the object which has to be reloaded
     * @return the reloaded object with all properties transferred or the original object if no reloading was needed
     */
    public Object reloadScriptingInstance(Object scriptingInstance) {
        Map<String, ReloadingMetadata> classMap = getClassMap();
        if (classMap.size() == 0) {
            return scriptingInstance;
        }

        ReloadingMetadata reloadMeta = classMap.get(scriptingInstance.getClass().getName());

        //This gives a minor speedup because we jump out as soon as possible
        //files never changed do not even have to be considered
        //not tained even once == not even considered to be reloaded
        if (isReloadCandidate(reloadMeta)) {

            //reload the class to get new static content if needed
            Class aclass = reloadScriptingClass(scriptingInstance.getClass());
            if (aclass.hashCode() == scriptingInstance.getClass().hashCode()) {
                //class of this object has not changed although
                // reload is enabled we can skip the rest now
                return scriptingInstance;
            }
            log.info("possible reload for " + scriptingInstance.getClass().getName());
            /*only recreation of empty constructor classes is possible*/
            try {
                //reload the object by instiating a new class and
                // assigning the attributes properly
                Object newObject = aclass.newInstance();

                /*now we shuffle the properties between the objects*/
                mapProperties(newObject, scriptingInstance);

                return newObject;
            } catch (Exception e) {
                log.error(e);
            }
        }
        return scriptingInstance;


    }

    /**
     * helper to map the properties wherever possible
     *
     * @param target
     * @param src
     */
    private void mapProperties(Object target, Object src) {
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
     * condition which marks a metadata as reload candidate
     */
    private boolean isReloadCandidate(ReloadingMetadata reloadMeta) {
        return reloadMeta != null && reloadMeta.getScriptingEngine() == ScriptingConst.ENGINE_TYPE_JAVA && reloadMeta.isTaintedOnce();
    }


    /**
     * reweaving of an existing woven class
     * by reloading its file contents and then reweaving it
     */
    public Class reloadScriptingClass(Class aclass) {
        ReloadingMetadata metadata = getClassMap().get(aclass.getName());
        if (metadata == null)
            return aclass;
        if (!metadata.isTainted()) {
            //if not tained then we can recycle the last class loaded
            return metadata.getAClass();
        }
        return loadScriptingClassFromFile(metadata.getSourcePath(), metadata.getFileName());
    }

    /**
     * recompiles and loads a scripting class from a given classname
     *
     * @param className the classname including the package
     * @return a valid class if the sources could be found null if nothing could be found
     */
    public Class loadScriptingClassFromName(String className) {
        Map<String, ReloadingMetadata> classMap = getClassMap();
        ReloadingMetadata metadata = classMap.get(className);
        if (metadata == null) {
            String fileName = className.replaceAll("\\.", File.separator) + ".java";

            //TODO this code can probably be replaced by the functionality
            //already given in the Groovy classloader, this needs further testing
            for (String pathEntry : this.scriptPaths) {

                Class retVal = (Class) loadScriptingClassFromFile(pathEntry, fileName);
                if (retVal != null) {
                    return retVal;
                }
            }

        } else {
            return reloadScriptingClass(metadata.getAClass());
        }
        return null;
    }

    /**
     * helper for accessing the reloading metadata map
     *
     * @return
     */
    private Map<String, ReloadingMetadata> getClassMap() {
        return FileChangedDaemon.getInstance().getClassMap();
    }

    /**
     * loads a class from a given sourceroot and filename
     *
     * @param sourceRoot the source search lookup path
     * @param file       the filename to be compiled and loaded
     * @return a valid class if it could be found, null if none was found
     */
    protected Class loadScriptingClassFromFile(String sourceRoot, String file) {
        //we load the scripting class from the given className
        File currentClassFile = new File(sourceRoot + File.separator + file);
        if (!currentClassFile.exists()) {
            return null;
        }
        log.info("Loading java file:" + file);

        Iterator<String> it = scriptPaths.iterator();

        Class retVal = null;
        //while(retVal == null && it.hasNext()) {
        //    String currentPath = it.next();
        try {
            //we initialize the compiler lazy
            //because the facade itself is lazy
            DynamicCompiler compiler = new CompilerFacade();
            retVal = compiler.compileFile(sourceRoot, classPath, file);
        } catch (ClassNotFoundException e) {
            //can be safely ignored
        }
        //}
        if (retVal != null) {
            ReloadingMetadata reloadingMetaData = new ReloadingMetadata();
            reloadingMetaData.setAClass(retVal);

            reloadingMetaData.setFileName(file);
            reloadingMetaData.setSourcePath(sourceRoot);
            reloadingMetaData.setTimestamp(currentClassFile.lastModified());
            reloadingMetaData.setTainted(false);
            reloadingMetaData.setScriptingEngine(ScriptingConst.ENGINE_TYPE_JAVA);
            getClassMap().put(retVal.getName(), reloadingMetaData);
        }

        return retVal;
    }
}
