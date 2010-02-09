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
import org.apache.myfaces.scripting.loaders.java.util.ExtendedLoopCnt;
import org.apache.myfaces.scripting.refresh.ReloadingMetadata;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          <p/>
 *          A  dependency scanner which utilizes the scan infrastructure
 *          from the core, multithreaded version
 */
public class JavaDependencyScannerMT implements ClassScanner {
    private static final int VALUE_POOL_SIZE = 3;
    static final int MAX_PARALLEL_SCANS = VALUE_POOL_SIZE;
    Semaphore threadCtrl = new Semaphore(MAX_PARALLEL_SCANS);


    List<String> _scanPaths = new LinkedList<String>();

    //we stack this to get an easier handling in a multithreaded environment
    final Stack<DependencyScanner> dependencyScanner = new Stack<DependencyScanner>();


    ScriptingWeaver _weaver;
    Log log = LogFactory.getLog(JavaDependencyScannerMT.class.getName());


    public JavaDependencyScannerMT(ScriptingWeaver weaver) {
        this._weaver = weaver;
        for (int cnt = 0; cnt < MAX_PARALLEL_SCANS; cnt++) {
            dependencyScanner.push(new DefaultDependencyScanner());
        }
    }


    public synchronized void scanPaths() {

        if (log.isInfoEnabled()) {
            log.info("[EXT-SCRITPING] starting class dependency scan");
        }
        long start = System.currentTimeMillis();
        final Set<String> possibleDynamicClasses = new HashSet<String>(_weaver.loadPossibleDynamicClasses());

        final ClassLoader loader = getClassLoader();

        WeavingContext.pushThreadingData();
        try {
            //we have not entirely achieved the performance numbers
            //we have for the non threaded version hence we
            //do not do it yet, we have to do a profiling
            //on a high number of classes to get the
            //correct values, and then we will take the version
            //which is faster

            String[] valuesArr = new String[VALUE_POOL_SIZE];
            int len = valuesArr.length;
            // int totalCnt = 0;

            ExtendedLoopCnt cnt = new ExtendedLoopCnt(0, 0, VALUE_POOL_SIZE);
            for (String dynamicClass : possibleDynamicClasses) {

                //if (threaded)
                valuesArr[cnt.getCnt()] = dynamicClass;
                if (cnt.getCnt() == (VALUE_POOL_SIZE - 1) || cnt.getTotalCnt() == (len - 1)) {
                    runScanThreaded(possibleDynamicClasses, loader, valuesArr, cnt.getCnt() + 1);
                }
                cnt.incTotalCnt();
                if (cnt.incCnt() == 0) {
                    valuesArr = new String[VALUE_POOL_SIZE];
                }
            }
            while (threadCtrl.availablePermits() != MAX_PARALLEL_SCANS) {
                try {
                    Thread.sleep(7);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            long end = System.currentTimeMillis();
            if (log.isInfoEnabled()) {
                log.info("[EXT-SCRITPING] class dependency scan finished, duration: " + (end - start) + " ms");
            }
        } finally {
            WeavingContext.cleanThreadingData();
        }
    }

    private final void runScanThreaded(final Set<String> possibleDynamicClasses, final ClassLoader loader, final String[] dynaClasses, final int noClasses) {

        try {


            threadCtrl.acquire();
            //problem with the thread locals set we have to shift all the needed constats
            //in and pass them into the thread

            (new Thread() {
                public void run() {

                    WeavingContext.popThreadingData();

                    DependencyScanner depScanner = dependencyScanner.pop();
                    try {
                        for (int cnt = 0; cnt < noClasses; cnt++) {
                            String dynaClass = dynaClasses[cnt];
                            Set<String> referrers = depScanner.fetchDependencies(loader, dynaClass, possibleDynamicClasses);
                            //we make it in two ops because if we do the self dependency
                            //removal in the scanner itself the code  should not break
                            referrers.remove(dynaClass);
                            if (!referrers.isEmpty()) {
                                WeavingContext.getFileChangedDaemon().getDependencyMap().addDependencies(dynaClass, referrers);
                            }
                        }
                    } finally {
                        dependencyScanner.push(depScanner);
                        threadCtrl.release();
                    }

                }
            }).start();
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    protected ClassLoader getClassLoader() {
        return new RecompiledClassLoader(Thread.currentThread().getContextClassLoader(), ScriptingConst.ENGINE_TYPE_JAVA, ".java");
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

      public void scanAndMarkChange() {

        final Set<String> possibleDynamicClasses = new HashSet<String>(_weaver.loadPossibleDynamicClasses());
        Map <Integer, Boolean> recompileMap = WeavingContext.getRefreshContext().getDaemon().getSystemRecompileMap();
        Map <String, ReloadingMetadata> classMap = WeavingContext.getRefreshContext().getDaemon().getClassMap();
        Boolean alreadyTainted = recompileMap.get(getEngineType());
        if(alreadyTainted != null && alreadyTainted) {
            return;
        }

        for(String clazz: possibleDynamicClasses) {
            if(!classMap.containsKey(clazz)) {
                recompileMap.put(getEngineType(), Boolean.TRUE);
            }
        }
    }

    public int getEngineType() {
        return ScriptingConst.ENGINE_TYPE_JAVA;
    }
}
