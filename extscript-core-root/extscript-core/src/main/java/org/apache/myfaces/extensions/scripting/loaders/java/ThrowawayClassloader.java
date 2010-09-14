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
package org.apache.myfaces.extensions.scripting.loaders.java;

import org.apache.myfaces.extensions.scripting.core.util.ClassUtils;
import org.apache.myfaces.extensions.scripting.core.util.FileUtils;
import org.apache.myfaces.extensions.scripting.core.util.WeavingContext;
import org.apache.myfaces.extensions.scripting.monitor.ClassResource;
import org.apache.myfaces.extensions.scripting.monitor.RefreshAttribute;
import org.apache.myfaces.extensions.scripting.monitor.RefreshContext;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.Level.*;

/**
 * we move the throw away mechanism into our classloader for cleaner code coverage
 * the idea is to throw away classloaders on demand if a class is reloaded
 * we throw it away at two stages
 * <p/>
 * first at call stage from outside if we manually load a class
 * and secondly internally if a class is reloaded by the system
 * or if something calls loadClass directly
 */
@JavaThrowAwayClassloader
@SuppressWarnings("unused")
public class ThrowawayClassloader extends ClassLoader {

    static final Logger _logger = Logger.getLogger(ThrowawayClassloader.class.getName());

    int _scriptingEngine;
    String _engineExtension;

    public ThrowawayClassloader(ClassLoader classLoader, int scriptingEngine, String engineExtension) {
        super(classLoader);

        synchronized (this.getClass()) {
            _scriptingEngine = scriptingEngine;
            _engineExtension = engineExtension;
        }
    }

    public ThrowawayClassloader(ClassLoader classLoader, int scriptingEngine, String engineExtension, boolean untaint) {
        this(classLoader, scriptingEngine, engineExtension);

    }

    ThrowawayClassloader() {
    }


    @Override
    public InputStream getResourceAsStream(String name) {
        File resource = new File(WeavingContext.getConfiguration().getCompileTarget().getAbsolutePath() + File.separator + name);
        if (resource.exists()) {
            try {
                return new FileInputStream(resource);
            } catch (FileNotFoundException e) {
                return super.getResourceAsStream(name);
            }
        }
        return super.getResourceAsStream(name);
    }

    @Override
    /**
     * load called either if the class is not loaded at all
     * or if the class has been recompiled (check upfront)
     */
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        //check if our class exists in the tempDir
        //we have to register ourselves temporarily because the trhow away classloader
        //can be called implicitely


        File target = WeavingContext.getConfiguration().resolveClassFile(className);
        if (target.exists()) {

            _logger.log(Level.FINE, "[EXT-SCRIPTING] target {0} exists", className);

            ClassResource data = WeavingContext.getFileChangedDaemon().getClassMap().get(className);

            //this check must be present because
            //the vm recycles old classloaders to load classes a anew
            //if we dont do it we get an exception
            //we cannot check here for the file timestamps because if we have import dependencies
            //it can happen that the compiler refreshes the forward dependend file as well
            //and then we reload the class in one file but reference it from an artifact
            //in another, it is better to check for the taint state instead
            if (data != null && !data.getRefreshAttribute().requiresRefresh()) {

                return data.getAClass();
            }
            //a load must happen anyway because the target was recompiled
            int fileLength;
            byte[] fileContent;
            FileInputStream iStream = null;
            try {
                //we cannot load while a compile is in progress
                //we have to wait until it is one
                synchronized (RefreshContext.COMPILE_SYNC_MONITOR) {
                    fileLength = (int) target.length();
                    fileContent = new byte[fileLength];
                    iStream = new FileInputStream(target);
                    int result = iStream.read(fileContent);
                    _logger.log(Level.FINER, "read {0} bytes", String.valueOf(result));
                }

            } catch (FileNotFoundException e) {
                throw new ClassNotFoundException(e.toString());
            } catch (IOException e) {
                throw new ClassNotFoundException(e.toString());
            } finally {
                if (iStream != null) {
                    try {
                        iStream.close();
                    } catch (Exception e) {
                        Logger log = Logger.getLogger(this.getClass().getName());
                        log.log(SEVERE, "", e);
                    }
                }
            }


            if (data != null) {
                File sourceFile = data.getFile();

                _logger.log(Level.FINER, "[EXT-SCRIPTING] loading class {0} from filesystem", className);

                Class retVal;

                //sometimes the classloader is recycled between requests due to being bound to the old class, we have to open a new classloader here just for the sake
                //to avoid conflicts
                retVal = (new ThrowawayClassloader(getParent(), _scriptingEngine, _engineExtension)).defineClass(className, fileContent, 0, fileLength);
                data.setAClass(retVal);
                data.getRefreshAttribute().executedRefresh();
                data.executeLastLoaded();
                return retVal;

            } else {
                //we store the initial reloading meta data information so that it is refreshed
                //later on, this we we cover dependent classes on the initial load
                return storeReloadableDefinitions(className, fileLength, fileContent);
            }
        }
        _logger.log(Level.FINER, "[EXT-SCRIPTING] target {0} does not exist", target.getAbsolutePath());
        return super.loadClass(className);
    }



    private Class<?> storeReloadableDefinitions(String className, int fileLength, byte[] fileContent) {
        Class retVal;
        retVal = super.defineClass(className, fileContent, 0, fileLength);
        ClassResource reloadingMetaData = new ClassResource();
        reloadingMetaData.setAClass(retVal);
        //find the source for the given class and then
        //store the filename
        String separator = FileUtils.getFileSeparatorForRegex();
        String fileName = className.replaceAll("\\.", separator);
        fileName = (fileName.indexOf("$") != -1) ? fileName.substring(0, fileName.indexOf("$")) : fileName;

        fileName = fileName.replaceAll("\\.", separator) + getStandardFileExtension();
        Collection<String> sourceDirs = WeavingContext.getConfiguration().getSourceDirs(_scriptingEngine);
        String rootDir = null;
        File sourceFile = null;
        for (String sourceDir : sourceDirs) {
            String fullPath = sourceDir + File.separator + fileName;
            sourceFile = new File(fullPath);
            if (sourceFile.exists()) {
                rootDir = sourceDir;
                break;
            }
        }

        if (rootDir == null) {
            Logger log = Logger.getLogger(this.getClass().getName());
            log.log(WARNING, "Warning source for class: {0} could not be found", className);
            return retVal;
        }

        reloadingMetaData.setFile(new File(rootDir + File.separator + fileName));
        reloadingMetaData.getRefreshAttribute().requestRefresh();
        reloadingMetaData.getRefreshAttribute().executedRefresh();

        reloadingMetaData.setScriptingEngine(_scriptingEngine);

        WeavingContext.getFileChangedDaemon().getClassMap().put(className, reloadingMetaData);
        reloadingMetaData.executeLastLoaded();
        return retVal;
    }

    protected String getStandardFileExtension() {
        return _engineExtension;
    }

    protected Class<?> findClassExposed(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }

    //some classloaders fail to resolve the resource properly, we have
    //to drag our local paths in to keep track of the compiled resources
    //for different scripting languages

    public URL getResource(String resource) {
        URL res = super.getResource(resource);
        if (res != null) return res;
        //if we do get a null value we try to remap to our custom paths
        if (!resource.endsWith(".class")) return null;
        resource = resource.substring(0, resource.length() - 6);
        resource = resource.replaceAll("\\/", ".");

        File clsFile = WeavingContext.getConfiguration().resolveClassFile(resource);
        if (!clsFile.exists()) return null;
        try {
            return clsFile.toURI().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
