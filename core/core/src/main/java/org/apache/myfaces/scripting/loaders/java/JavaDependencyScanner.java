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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.scripting.api.ClassScanListener;
import org.apache.myfaces.scripting.api.ClassScanner;
import org.apache.myfaces.scripting.api.ScriptingConst;
import org.apache.myfaces.scripting.api.ScriptingWeaver;
import org.apache.myfaces.scripting.core.dependencyScan.DefaultDependencyScanner;
import org.apache.myfaces.scripting.core.dependencyScan.DependencyScanner;
import org.apache.myfaces.scripting.core.util.WeavingContext;

import java.util.*;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          A  dependency scanner which utilizes the scan infrastructure
 *          from the core
 */
public class JavaDependencyScanner implements ClassScanner {

    List<String> _scanPaths = new LinkedList<String>();


    DependencyScanner _dependecyScanner = new DefaultDependencyScanner();

    ScriptingWeaver _weaver;
    Log log = LogFactory.getLog(JavaDependencyScanner.class.getName());


    public JavaDependencyScanner(ScriptingWeaver weaver) {
        this._weaver = weaver;

    }

    public synchronized void scanPaths() {
        //only one dependency check per refresh makes sense in our case
        if(WeavingContext.getRefreshContext().isDependencyScanned(getEngineType())) {
            return;
        } else {
            WeavingContext.getRefreshContext().setDependencyScanned(getEngineType(), true);
        }


        if (log.isInfoEnabled()) {
            log.info("[EXT-SCRITPING] starting class dependency scan");
        }
        long start = System.currentTimeMillis();
        final Set<String> possibleDynamicClasses = new HashSet<String>(_weaver.loadPossibleDynamicClasses());

        final ClassLoader loader = getClassLoader();
        for (String dynamicClass : possibleDynamicClasses) {
            runScan(possibleDynamicClasses, loader, dynamicClass);
        }

        long end = System.currentTimeMillis();
        if (log.isInfoEnabled()) {
            log.info("[EXT-SCRITPING] class dependency scan finished, duration: " + (end - start) + " ms");
        }

    }

    protected int getEngineType() {
        return ScriptingConst.ENGINE_TYPE_JAVA;
    }

    private final void runScan(final Set<String> possibleDynamicClasses, final ClassLoader loader, String dynamicClass) {

        Set<String> referrers = _dependecyScanner.fetchDependencies(loader, dynamicClass, possibleDynamicClasses);
        //we make it in two ops because if we do the self dependency
        //removal in the scanner itself the code  should not break
        referrers.remove(dynamicClass);
        if (!referrers.isEmpty()) {
            WeavingContext.getFileChangedDaemon().getDependencyMap().addDependencies(dynamicClass, referrers);
        }

    }


    protected ClassLoader getClassLoader() {
        return new RecompiledClassLoader(Thread.currentThread().getContextClassLoader(), getEngineType(), ".java");
    }


    public void clearListeners() {
    }

    public void addListener(ClassScanListener listener) {

    }

    public void addScanPath(String scanPath) {
        _scanPaths.add(scanPath);
    }

    public synchronized void scanClass(Class clazz) {
        //not needed anymore since we only rely on full scans and full recompile now
    }
}
