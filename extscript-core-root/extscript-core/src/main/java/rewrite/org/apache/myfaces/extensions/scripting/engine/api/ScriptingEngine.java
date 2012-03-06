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
package rewrite.org.apache.myfaces.extensions.scripting.engine.api;

import rewrite.org.apache.myfaces.extensions.scripting.engine.dependencyScan.api.DependencyRegistry;
import rewrite.org.apache.myfaces.extensions.scripting.engine.dependencyScan.core.ClassDependencies;
import rewrite.org.apache.myfaces.extensions.scripting.monitor.ClassResource;

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
    
    public List<String> getSourcePaths();

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
    public void compile();

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
}