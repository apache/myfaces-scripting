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
import java.util.concurrent.Semaphore;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          A  dependency scanner which utilizes the scan infrastructure
 *          from the core
 */
public class JavaDependencyScanner implements ClassScanner {

    List<String> _scanPaths = new LinkedList<String>();
    static final int MAX_PARALLEL_SCANS = 1;
    //Semaphore threadCtrl = new Semaphore(MAX_PARALLEL_SCANS);

    //we stack this to get an easier handling in a multithreaded environment
    final Stack<DependencyScanner> dependencyScanner = new Stack<DependencyScanner>();


    ScriptingWeaver _weaver;
    Log log = LogFactory.getLog(JavaDependencyScanner.class.getName());


    public JavaDependencyScanner(ScriptingWeaver weaver) {
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
        //TODO we have to probably set the context classloader upfront
        //otherwise the classes are not found
        final ClassLoader loader = new RecompiledClassLoader(Thread.currentThread().getContextClassLoader(), ScriptingConst.ENGINE_TYPE_JAVA);

        String[] dynamicClassArr = new String[3];
        for (String dynamicClass : possibleDynamicClasses) {
            final String dynaClass = dynamicClass;

            //TODO parallize this
            //we have to shift the threadlocal data of the weaving context into
            //every thread there is and push it into the threadlocals there
            //I will leave this for now open for the next performance round

            //prefill an array which keeps the in params
            //try {
            // threadCtrl.acquire();
            //problem with the thread locals set we have to shift all the needed constats
            //in and pass them into the thread

            //  (new Thread() {
            //      public void run()
            //      {
           
            DependencyScanner depScanner = dependencyScanner.pop();
            try {
                Set<String> referrers = depScanner.fetchDependencies(loader, dynaClass, possibleDynamicClasses);
                //we make it in two ops because if we do the self dependency
                //removal in the scanner itself the code  should not break
                referrers.remove(dynaClass);
                if (!referrers.isEmpty()) {
                    WeavingContext.getFileChangedDaemon().getDependencyMap().addDependencies(dynaClass, referrers);
                }
            } finally {
                dependencyScanner.push(depScanner);
                //threadCtrl.release();
            }
            //    }
            // }).start();
            //} catch (InterruptedException e) {
            //    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            //}

        }
        long end = System.currentTimeMillis();
        if (log.isInfoEnabled()) {
            log.info("[EXT-SCRITPING] class dependency scan finished, duration: " + (end - start) + " ms");
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
