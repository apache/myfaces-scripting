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

package rewrite.org.apache.myfaces.extensions.scripting.core.engine;

import rewrite.org.apache.myfaces.extensions.scripting.core.api.ScriptingConst;
import rewrite.org.apache.myfaces.extensions.scripting.core.context.WeavingContext;
import rewrite.org.apache.myfaces.extensions.scripting.core.engine.api.ScriptingEngine;
import rewrite.org.apache.myfaces.extensions.scripting.core.engine.dependencyScan.StandardDependencyScanner;
import rewrite.org.apache.myfaces.extensions.scripting.core.engine.dependencyScan.api.DependencyScanner;
import rewrite.org.apache.myfaces.extensions.scripting.core.engine.dependencyScan.filter.WhitelistFilter;
import rewrite.org.apache.myfaces.extensions.scripting.core.engine.dependencyScan.loaders.ScannerClassloader;
import rewrite.org.apache.myfaces.extensions.scripting.core.engine.dependencyScan.registry.ExternalFilterDependencyRegistry;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public abstract class BaseScanner
{
    List<String> _scanPaths = new LinkedList<String>();DependencyScanner _depencyScanner = new StandardDependencyScanner();Logger _log = Logger.getLogger(JavaDependencyScanner.class.getName());

    public abstract int getEngineType();

    public abstract String getFileEnding();

    public synchronized void scanPaths() {
        //only one dependency check per refresh makes sense in our case
       /* if (WeavingContext.getRefreshContext().isDependencyScanned(getEngineType())) {
            return;
        } else {
            WeavingContext.getRefreshContext().setDependencyScanned(getEngineType(), true);
        }*/
        ScriptingEngine engine = WeavingContext.getInstance().getEngine(ScriptingConst.ENGINE_TYPE_JSF_JAVA);

        if (_log.isLoggable(Level.INFO)) {
            _log.info("[EXT-SCRITPING] starting class dependency scan");
        }
        long start = System.currentTimeMillis();
        final Set<String> possibleDynamicClasses = new HashSet<String>(engine.getPossibleDynamicClasses());

        final ClassLoader loader = getClassLoader();
        for (String dynamicClass : possibleDynamicClasses) {
            runScan(possibleDynamicClasses, loader, dynamicClass);
        }

        long end = System.currentTimeMillis();
        if (_log.isLoggable(Level.FINE)) {
            _log.log(Level.FINE, "[EXT-SCRITPING] class dependency scan finished, duration: {0} ms", Long.toString(end - start));
        }

    }

    private void runScan(final Set<String> possibleDynamicClasses, final ClassLoader loader, String dynamicClass) {
        //TODO implement the dep registry
        ExternalFilterDependencyRegistry scanRegistry = (ExternalFilterDependencyRegistry) WeavingContext.getInstance()
        .getEngine(getEngineType()).getDependencyRegistry();

        scanRegistry.clearFilters();
        //We have to dynamically readjust the filters
        scanRegistry.addFilter(new WhitelistFilter(possibleDynamicClasses));
        _depencyScanner.fetchDependencies(loader, getEngineType(), dynamicClass,
                WeavingContext.getInstance().getEngine(getEngineType()).getDependencyRegistry());
    }

    protected ClassLoader getClassLoader() {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<ScannerClassloader>()
            {
                public ScannerClassloader run()
                {
                    return new ScannerClassloader(Thread.currentThread().getContextClassLoader(), getEngineType(),
                            getFileEnding(), WeavingContext.getInstance().getConfiguration().getCompileTarget());
                }
            });
        } catch (PrivilegedActionException e) {
            _log.log(Level.SEVERE,"", e);
        }
        return null;
    }

    public void addScanPath(String scanPath) {
        _scanPaths.add(scanPath);
    }
}
