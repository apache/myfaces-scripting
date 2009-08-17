package org.apache.myfaces.groovyloader.core

import org.apache.myfaces.scripting.jsf.ScriptingConst
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

/**
 * A worker daemon which
 * marks files as changed
 */
public class FileChangedDaemon {
    Map classMap

    boolean running
    Thread daemonThread

    Log log = LogFactory.getLog(FileChangedDaemon.class)


    public FileChangedDaemon(Map classMap) {
        this.classMap = classMap
    }

    public start() {
        if (daemonThread != null && daemonThread.alive) return;
        running = true
        daemonThread = Thread.startDaemon {
            while (running) {
                try {
                    sleep ScriptingConst.TAINT_INTERVAL
                    if (classMap == null || classMap.size() == 0)
                        continue;
                } catch (e) {
                    //this looks weird at the first sight but we get a shutdown npe here
                    //in caused by the groovy engine kickstarting  in case of an interruption we terminate the
                    //thread, reinstantiation can happen if we are not in a shutdown phase
                    return;
                }

                classMap.each {
                    if (!it.value.tainted) {

                        File proxyFile = new File(it.value.fileName)
                        it.value.tainted = (proxyFile.lastModified() != it.value.timestamp);
                        if (it.value.tainted) {
                            it.value.taintedOnce = true;
                            log.info("tainting $it.value.fileName")
                        }
                        it.value.timestamp = proxyFile.lastModified()
                    }
                }
            }
        }
    }


    public stop() {
        running = false
    }

}