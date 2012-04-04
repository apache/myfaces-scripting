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
package org.apache.myfaces.extensions.scripting.core.engine.api;

import org.apache.myfaces.extensions.scripting.core.api.ReloadingStrategy;
import org.apache.myfaces.extensions.scripting.core.engine.dependencyScan.api.DependencyRegistry;
import org.apache.myfaces.extensions.scripting.core.engine.dependencyScan.core.ClassDependencies;
import org.apache.myfaces.extensions.scripting.core.monitor.ClassResource;
import org.apache.myfaces.extensions.scripting.core.monitor.WatchedResource;

import javax.servlet.ServletContext;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public interface ScriptingEngine
{
    public void init(ServletContext context);

    public Collection<String> getPossibleDynamicClasses();
    
    public Collection<String> getSourcePaths();

    public Map<String, ClassResource> getWatchedResources();

    /**
     * @return the identifier for the engine type
     * each identifier must be unique to the system
     */
    public int getEngineType();

    /**
     * returns the file ending sufficient for this engine
     * @return
     */
    public String getFileEnding();

    /**
     * runs a full scan of files to get a list of files which need to be processed
     * for the future
     */
    public void scanForAddedDeleted();

    /**
     * runs the compile cycle for this engine
     */
    public CompilationResult compile();

    /**
     * checks if the current engine has tainted classes
     * @return
     */
    public boolean isTainted();

    /**
     * checks if the current engine has a need for a recompile of certain classes
     * @return
     */
    public boolean needsRecompile();

    /**
     * gets the dependency map hosted in this engine
     *
     * @return the dependency map
     */
    public ClassDependencies getDependencyMap();

    /**
     * fetches the dependency registry
     *
     * @return the dependency registry
     */
    public DependencyRegistry getDependencyRegistry();

    /**
     * Scan dependencies for this submodule
     */
    public void scanDependencies();

    /**
     * mark the classes which are dependend
     * as tainted according to the dependency graph
     * The function has to walk the graph recursively
     * according to its state and mark all backward references
     * as tainted.
     */
    public void markTaintedDependencies();

    /**
     *
     * @return a string representation
     * of the corresponding engine
     */
    public String getEngineTypeAsStr();

    /**
     * loads the basic strategy which hosts also the property copying algorithm
     *
     * @return the basic strategy
     */
    public ReloadingStrategy getBasicReloadingStrategy();

    /**
     * detects whether a given object is an artifact of a given object
     * @return
     */
    public boolean isArtifactOfEngine(Object artifact);

    /**
     * engine related copy properties, which takes
     * certain language constructs into consideration.
     * Groovy for instance has extra props for meta classes
     * which should not be copied.
     *
     * @param dest the destination
     * @param src  the source
     */
    public void copyProperties(Object dest, Object src);
}
