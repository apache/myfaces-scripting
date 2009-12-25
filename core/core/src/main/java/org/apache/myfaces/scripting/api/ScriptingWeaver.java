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
package org.apache.myfaces.scripting.api;

import java.util.Collection;

/**
 * @author werpu
 * @date: 15.08.2009
 * <p/>
 * Central interface to the scripting layer
 * this class is a weaver which allows to trigger
 * the scripting layer in various situations
 * of the JSF interception points
 * <p/>
 * The scripting weaver replaces the classloader for those instances
 * because custom classloaders are inherently problematic in web containers
 */
public interface ScriptingWeaver {

    /**
     * appends a custom script search path to the original one
     *
     * @param scriptPaths
     * @deprecates
     */
    public void appendCustomScriptPath(String scriptPaths);


    /**
     * @param o            the object which has to be reloaded
     * @param artefactType an identifier for the artefact type so that its reloading strategies can
     *                     be adjusted depending on the type of artefact which has to be processed, we have to pass down
     *                     this artefact because we cannot rely on instanceof here for several reasons first we do not know
     *                     if a managed bean does not implement as well one of the artefact interfaces for one reason or the other
     *                     secondly how do we deal with future extensions which provide new artefacts we cannot
     *                     bind the code to just one implementation, hence we add some kind of type identifier here as well
     * @return reloads an existing objects with its attributes
     *         and assigns the reloaded class to the new object
     *         <p/>
     *         note, the new object must not be the same as the original one
     *         it can be a shallow clone with a new class instead
     */
    public Object reloadScriptingInstance(Object o, int artefactType);

    /**
     * reloads an existing class if needed
     * if no new class exists the original class is given back
     *
     * @param aclass the class which is likely to be reloaded
     * @return a new class or the same if no refresh has to be performed
     */
    public Class reloadScriptingClass(Class aclass);

    /**
     * loads a scripting class from a given className
     * note, this method probably will be dropped in the long
     * run
     *
     * @param className the classname including the package
     * @return a class instance of the file
     */
    public Class loadScriptingClassFromName(String className);


    /**
     * returns the engine type for this weaver
     *
     * @return
     */
    public int getScriptingEngine();


    /**
     * checks wether a given class can be reloaded
     * from this weaver or not
     *
     * @param clazz
     * @return
     */
    public boolean isDynamic(Class clazz);


    public ScriptingWeaver getWeaverInstance(Class weaverClass);

    /**
     * full annotation scan
     * at startup once the system is initialized
     */
    public void fullClassScan();


    /**
     * do a full recompile of changed resources instead of a
     * simply compile per file
     */
    public void fullRecompile();


    /**
     * callback for artefacting request refreshes
     * some artefacts should be refreshed or cleared upon
     * request time, others can be dealt with on on demand time
     */
    public void requestRefresh();


    /**
     * loads a list of possible dynamic classes
     * for the current given state of the source dirs
     * 
     * @return a list of classes representing the current source state
     */
    public Collection<String> loadPossibleDynamicClasses();

}
