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
package org.apache.myfaces.extensions.scripting.core.engine;

import org.apache.commons.io.FilenameUtils;
import org.apache.myfaces.extensions.scripting.core.common.util.ClassUtils;
import org.apache.myfaces.extensions.scripting.core.common.util.FileUtils;
import org.apache.myfaces.extensions.scripting.core.engine.dependencyScan.api.DependencyRegistry;
import org.apache.myfaces.extensions.scripting.core.engine.dependencyScan.core.ClassDependencies;
import org.apache.myfaces.extensions.scripting.core.engine.dependencyScan.registry.DependencyRegistryImpl;
import org.apache.myfaces.extensions.scripting.core.monitor.ClassResource;

import javax.servlet.ServletContext;
import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public abstract class BaseEngine
{
    CopyOnWriteArrayList<String> _sourcePaths = new CopyOnWriteArrayList<String>();
    Map<String, ClassResource> _watchedResources = new ConcurrentHashMap<String, ClassResource>();
    //both belong together but the dependencyregistry is just
    //a wrapper for the dep map with additional functionality
    ClassDependencies _dependencyMap = new ClassDependencies();
    DependencyRegistry _dependencyRegistry = new DependencyRegistryImpl(getEngineType(), _dependencyMap);

    Logger log = Logger.getLogger(this.getClass().getName());


    protected BaseEngine()
    {

    }

    public Map<String, ClassResource> getWatchedResources()
    {
        return _watchedResources;
    }

    public List<String> getSourcePaths()
    {
        return _sourcePaths;
    }

    public abstract String getFileEnding();

    public abstract int getEngineType();

    /**
     * @return a collection of possible dynamic classes
     */
    public Collection<String> getPossibleDynamicClasses()
    {
        return _watchedResources.keySet();
    }

    /**
     * runs a full scan of files to get a list of files which need to be processed
     * for the future
     */
    public void scanForAddedDeleted()
    {
        Set<String> processedClasses = new HashSet<String>();
        processedClasses.addAll(_watchedResources.keySet());

        for (String sourcePath : getSourcePaths())
        {
            Collection<File> sourceFiles = FileUtils.fetchSourceFiles(new File(sourcePath), "*." + getFileEnding());

            for (File sourceFile : sourceFiles)
            {
                ClassResource classToProcess = new ClassResource();
                classToProcess.setFile(sourceFile);


                classToProcess.setScriptingEngine(getEngineType());
                if (!_watchedResources.containsKey(classToProcess.getIdentifier()))
                {
                    _watchedResources.put(classToProcess.getIdentifier(), classToProcess);
                } else
                {
                    processedClasses.remove(classToProcess.getIdentifier());

                    classToProcess = _watchedResources.get(classToProcess.getIdentifier());
                }
                if (classToProcess.needsRecompile())
                {
                    //TODO add entry for logging component here
                    log.info("[EXT-SCRIPTING] tainting " + classToProcess.getIdentifier());
                    classToProcess.setTainted(true);
                    classToProcess.setChangedForCompile(true);
                }
            }
        }
        for (String deleted : processedClasses)
        {
            _watchedResources.remove(deleted);
        }

    }

    /**
     * checks whether we have resources which are in need of a recompile
     *
     * @return
     */
    public boolean needsRecompile()
    {
        //TODO buffer this from scan
        for (Map.Entry<String, ClassResource> resource : _watchedResources.entrySet())
        {
            if (resource.getValue().needsRecompile()) return true;
        }
        return false;
    }

    /**
     * checks whether we have resources which are tainted
     *
     * @return
     */
    public boolean isTainted()
    {
        //TODO buffer this from scan
        for (Map.Entry<String, ClassResource> resource : _watchedResources.entrySet())
        {
            if (resource.getValue().isTainted()) return true;
        }
        return false;
    }

    public DependencyRegistry getDependencyRegistry()
    {
        return _dependencyRegistry;
    }

    public void setDependencyRegistry(DependencyRegistry dependencyRegistry)
    {
        _dependencyRegistry = dependencyRegistry;
    }

    public ClassDependencies getDependencyMap()
    {
        return _dependencyMap;
    }

    public void setDependencyMap(ClassDependencies dependencyMap)
    {
        _dependencyMap = dependencyMap;
    }

    /**
     * marks all the dependencies of the tainted objects
     * also as tainted to allow proper refreshing.
     */
    public void markTaintedDependencies()
    {
        //basic tainted set by the full scall
        Set<String> _processedClasses = new HashSet<String>();
        for (Map.Entry<String, ClassResource> entry : _watchedResources.entrySet())
        {
            //TODO add entry for logging component here
            ClassResource resource = entry.getValue();
            if (!resource.isChangedForCompile()) continue;
            resource.setChangedForCompile(false);
            log.info("[EXT-SCRIPTING] tainting dependency " + resource.getIdentifier());
            resource.setTainted(true);
            //classname
            String identifier = resource.getIdentifier();
            if (_processedClasses.contains(identifier)) continue;
            markDependencies(_processedClasses, identifier);

        }

    }

    /*marks all backward dependencies of the existing class*/
    private void markDependencies(Set<String> _processedClasses, String identifier)
    {
        Set<String> referringClasses = _dependencyMap.getReferringClasses(identifier);
        if (referringClasses == null) return;
        for (String referringClass : referringClasses)
        {
            if (_processedClasses.contains(referringClass)) continue;
            ClassResource toTaint = _watchedResources.get(referringClass);
            if (toTaint == null) continue;
            //TODO add entry for logging component here
            if (toTaint.isTainted())
            {
                log.info("[EXT-SCRIPTING] dependency already tainted:" + toTaint.getIdentifier());
                _processedClasses.add(toTaint.getIdentifier());
                continue;
            }
            toTaint.setTainted(true);
            toTaint.setChangedForCompile(false);
            log.info("[EXT-SCRIPTING] tainting dependency " + toTaint.getIdentifier());
            _processedClasses.add(toTaint.getIdentifier());
            markDependencies(_processedClasses, toTaint.getIdentifier());
        }

    }

    protected void initPaths(ServletContext context, String initParam, String defaultValue)
    {
        String pathSeparatedList = context.getInitParameter(initParam);
        pathSeparatedList = (pathSeparatedList != null) ? pathSeparatedList : defaultValue;
        if (pathSeparatedList.equals(defaultValue))
        {
            URL resource = ClassUtils.getContextClassLoader().getResource("./");
            pathSeparatedList = FilenameUtils.normalize(resource.getPath() + "../.." + defaultValue);
        }
        String[] paths = pathSeparatedList.split(",");
        for (String path : paths)
        {
            getSourcePaths().add(path);
        }
    }
}
