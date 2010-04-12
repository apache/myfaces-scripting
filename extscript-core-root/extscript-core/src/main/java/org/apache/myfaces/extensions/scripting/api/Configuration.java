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
package org.apache.myfaces.extensions.scripting.api;

import org.apache.myfaces.extensions.scripting.core.util.FileUtils;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Configuration class for our ext-scripting system.
 * It keeps all internal configuration data needed
 * by the various part of the system.
 * <p/>
 * It is pre initialized in our init stages
 * and later accessed only readonly.
 */

public class Configuration {

    /**
     * the source dirs per scripting engine
     */
    volatile Map<Integer, CopyOnWriteArrayList<String>> _sourceDirs = new ConcurrentHashMap<Integer, CopyOnWriteArrayList<String>>();

    /**
     * the target compile path
     */
    volatile File _compileTarget = FileUtils.getTempDir();

    /**
     * if set to true we do an initial compile step upon loading
     */
    boolean _initialCompile = true;

    /**
     * the package whitelist used by our system
     * to determine which packages are under control.
     * <p/>
     * Note an empty whitelist means, all packages with sourcedirs attached to.
     */
    Set<String> _packageWhiteList = new HashSet<String>();

    /**
     * Optional additional classpath for the compilers
     * to be able to pick up additional compile jars
     */
    List<String> _additionalClassPath = new LinkedList<String>();

    /**
     * we keep track of separate resource dirs
     * for systems which can use resource loaders
     * <p/>
     * so that we can load various resources as well
     * from separate source directories instead
     */
    volatile List<String> _resourceDirs = new CopyOnWriteArrayList<String>();

    @SuppressWarnings("unchecked")
    public Collection<String> getSourceDirs(int scriptingEngine) {
        Collection<String> retVal = _sourceDirs.get(scriptingEngine);
        if (retVal == null) return Collections.EMPTY_SET;
        return retVal;
    }

    /**
     * returns a set of whitelisted subdirs hosting the source
     *
     * @param scriptingEngine the scripting engine for which the dirs have to be determined
     *                        (note every scripting engine has a unique integer value)
     * @return the current whitelisted dirs hosting the sources
     */
    public Collection<String> getWhitelistedSourceDirs(int scriptingEngine) {
        List<String> origSourceDirs = _sourceDirs.get(scriptingEngine);
        if (_packageWhiteList.isEmpty()) {
            return origSourceDirs;
        }

        return mergeWhitelisted(origSourceDirs);
    }

    /**
     * merges the whitelisted packages with the sourcedirs and generates a final list
     * which left join of both sets - the ones which do not exist in reality
     *
     * @param origSourceDirs the original source dirs
     * @return the joined existing subset of all directories which exist
     */
    private Collection<String> mergeWhitelisted(List<String> origSourceDirs) {
        List<String> retVal = new ArrayList<String>(_packageWhiteList.size() * origSourceDirs.size() + origSourceDirs.size());

        for (String whitelisted : _packageWhiteList) {
            whitelisted = whitelisted.replaceAll("\\.", FileUtils.getFileSeparatorForRegex());
            for (String sourceDir : origSourceDirs) {
                String newSourceDir = sourceDir + File.separator + whitelisted;
                if ((new File(newSourceDir)).exists()) {
                    retVal.add(newSourceDir);
                }
            }
        }
        return retVal;
    }

    /**
     * Add a new source dir for the corresponding scripting engine
     *
     * @param scriptingEngine integer value marking the corresponding engine
     * @param sourceDir       the source directory added to the existing source dir list
     */
    public void addSourceDir(int scriptingEngine, String sourceDir) {
        CopyOnWriteArrayList<String> dirs = _sourceDirs.get(scriptingEngine);
        if (dirs == null) {
            dirs = new CopyOnWriteArrayList<String>();
            _sourceDirs.put(scriptingEngine, dirs);
        }
        dirs.add(sourceDir);
    }

    public File getCompileTarget() {
        return _compileTarget;
    }

    public void addResourceDir(String dir) {
        _resourceDirs.add(dir);
    }

    public List<String> getResourceDirs() {
        return _resourceDirs;
    }

    public boolean isInitialCompile() {
        return _initialCompile;
    }

    public void setInitialCompile(boolean initialCompile) {
        this._initialCompile = initialCompile;
    }

    public void addWhitelistPackage(String pkg) {
        _packageWhiteList.add(pkg);
    }

    public Set<String> getPackageWhiteList() {
        return _packageWhiteList;
    }

    public void setPackageWhiteList(Set<String> packageWhiteList) {
        this._packageWhiteList = packageWhiteList;
    }

    public void addAdditionalClassPath(String path) {
        _additionalClassPath.add(path);
    }

    public List<String> getAdditionalClassPath() {
        return _additionalClassPath;
    }

    public void setAdditionalClassPath(List<String> additionalClassPath) {
        this._additionalClassPath = additionalClassPath;
    }
}
