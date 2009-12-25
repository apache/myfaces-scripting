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

import org.apache.myfaces.scripting.api.ClassScanListener;
import org.apache.myfaces.scripting.api.ClassScanner;
import org.apache.myfaces.scripting.api.ScriptingConst;
import org.apache.myfaces.scripting.api.ScriptingWeaver;
import org.apache.myfaces.scripting.core.dependencyScan.DefaultDependencyScanner;
import org.apache.myfaces.scripting.core.dependencyScan.DependencyScanner;
import org.apache.myfaces.scripting.core.util.WeavingContext;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          A  dependency scanner which utilizes the scan infrastructure
 *          from the core
 */
public class JavaDependencyScanner implements ClassScanner {

    List<String> _scanPaths = new LinkedList<String>();
    DependencyScanner dependencyScanner = new DefaultDependencyScanner();

    ScriptingWeaver _weaver;


    public JavaDependencyScanner(ScriptingWeaver weaver) {
        this._weaver = weaver;
    }

    public synchronized void scanPaths() {
        Set<String> possibleDynamicClasses = new HashSet<String>(_weaver.loadPossibleDynamicClasses());
        //TODO we have to probably set the context classloader upfront
        //otherwise the classes are not found
        ClassLoader loader = new RecompiledClassLoader(Thread.currentThread().getContextClassLoader(), ScriptingConst.ENGINE_TYPE_JAVA);


        for (String dynamicClass : possibleDynamicClasses) {
            Set<String> referrers = dependencyScanner.fetchDependencies(loader, dynamicClass, possibleDynamicClasses);
            //we make it in two ops because if we do the self dependency
            //removal in the scanner itself the code  should not break
            referrers.remove(dynamicClass);
            if (!referrers.isEmpty()) {
                WeavingContext.getFileChangedDaemon().getDependencyMap().addDependencies(dynamicClass, referrers);
            }
        }

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
