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
package org.apache.myfaces.scripting.refresh;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.scripting.api.ScriptingConst;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;



/**
 * @author werpu
 *         Reimplementation of the file changed daemon thread
 *         in java, the original one was done in groovy
 *         this threads purpose is to watch the files
 *         loaded by the various engine loaders for
 *         for file changes and then if one has changed we have to mark
 *         it for further processing
 */
public class FileChangedDaemon extends Thread {

    static FileChangedDaemon instance = null;

    Map<String, ReloadingMetadata> classMap = Collections.synchronizedMap(new HashMap<String, ReloadingMetadata>());
    boolean running = false;
    Log log = LogFactory.getLog(FileChangedDaemon.class);


    public static synchronized FileChangedDaemon getInstance() {
        if (instance == null) {
            instance = new FileChangedDaemon();
            instance.setDaemon(true);
        }
        if (!instance.isRunning() || !instance.isAlive()) {
            instance.start();
        }
        return instance;
    }

    public void start() {
        setRunning(true);
        log.info("Dynamic reloading watch daemon is starting");
        super.start();
    }

    public void run() {
        while (running) {
            try {
                Thread.sleep(ScriptingConst.TAINT_INTERVAL);
            } catch (InterruptedException e) {
                //if the server shuts down while we are in sleep we get an error
                //which we better should swallow
            }
            if (classMap == null || classMap.size() == 0)
                continue;

            for (Map.Entry<String, ReloadingMetadata> it : this.classMap.entrySet()) {
                if (!it.getValue().isTainted()) {

                    File proxyFile = new File(it.getValue().getFileName());
                    it.getValue().tainted = (proxyFile.lastModified() != it.getValue().getTimestamp());
                    if (it.getValue().isTainted()) {
                        it.getValue().setTaintedOnce(true);
                        log.info("Tainting:"+it.getValue().getFileName());
                    }
                    it.getValue().setTimestamp(proxyFile.lastModified());
                }
            }
        }
        log.info("Dynamic reloading watch daemon is shutting down");
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
    

    public Map<String, ReloadingMetadata> getClassMap() {
        return classMap;
    }

    public void setClassMap(Map<String, ReloadingMetadata> classMap) {
        this.classMap = classMap;
    }

}

