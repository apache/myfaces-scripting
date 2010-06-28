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
package org.apache.myfaces.extensions.scripting.monitor;

import org.apache.myfaces.extensions.scripting.api.ScriptingConst;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * data structure which holds the loaded data
 * for our taint thread
 *
 * @author Werner Punz
 */
public class RefreshAttribute implements Cloneable {

    /**
     * <p>The timestamp in milliseconds of the last time that the bean
     * definition that this attribute belongs to has been requested to
     * refresh itself.</p>
     */
    private long refreshRequested;

    /**
     * <p>The timestamp in milliseconds of the last time that the bean
     * definition that this attribute belongs to has been actually
     * refreshed.</p>
     */
    private long refreshExecuted;


    //Resource facility which has to be watched over
    //TODO this will be moved into a separate resource facility
    
    /*
    * volatile due to the ram concurrency behavior
    * of the instance vars jdk 5+
    */
    volatile String _fileName = "";
    volatile String _sourcePath = "";
    volatile Class _aClass = null;
    volatile long _timestamp = 0l;
    volatile int _scriptingEngine = ScriptingConst.ENGINE_TYPE_JSF_NO_ENGINE;



    /**
     * <p>By calling this method the user is able to request another refresh. Note that
     * this doesn't cause the bean factory to refresh the bean definition immediately,
     * but rather it just signals a request. The bean definition will be refreshed once
     * the bean factory has to deal with the next bean request (i.e. a call to
     * getBean()).</p>
     */
    public void requestRefresh() {
        refreshRequested = System.currentTimeMillis();
    }

    /**
     * <p>Returns the timestamp in milliseconds of the last time that a refresh operation
     * has been requested.</p>
     *
     * @return the timestamp in milliseconds of the last refresh request
     */
    public long getRequestedRefreshDate() {
        return refreshRequested;
    }

    /**
     * <p>By calling this method the user indicates that the according bean definition
     * has just been refreshed, which means that the method #{@link #requiresRefresh()}
     * will return <code>false</code> until the user requests the next refresh.</p>
     */
    public void executedRefresh() {
        refreshExecuted = System.currentTimeMillis();
    }

    /**
     * <p>Returns the timestamp in milliseconds of the last time that a refresh operation
     * has been executed.</p>
     *
     * @return the timestamp in milliseconds of the last executed refresh operation
     */
    public long getExecutedRefreshDate() {
        return refreshExecuted;
    }

    /**
     * <p>Determines whether a refresh is required, i.e. whether the user has requested
     * another refresh operation by calling {@link #requestRefresh()} recently. Note that
     * a call to this method only determines whether the bean definition on its own has
     * to be refreshed (i.e. it doesn't even consider a particular bean instance).</p>
     *
     * @return whether a refresh call is required
     */
    public boolean requiresRefresh() {
        return getExecutedRefreshDate() < getRequestedRefreshDate();
    }

   //--- todo move this into a separate resource handling facility

    public String getFileName() {
        return _fileName;
    }

    public void setFileName(String fileName) {
        this._fileName = fileName;
    }

    public Class getAClass() {
        return _aClass;
    }

    public void setAClass(Class aClass) {
        this._aClass = aClass;
    }

    public long getTimestamp() {
        return _timestamp;
    }

    public void setTimestamp(long timestamp) {
        this._timestamp = timestamp;
    }

    public int getScriptingEngine() {
        return _scriptingEngine;
    }

    public void setScriptingEngine(int scriptingEngine) {
        this._scriptingEngine = scriptingEngine;
    }

    public String getSourcePath() {
        return _sourcePath;
    }

    public void setSourcePath(String sourcePath) {

        this._sourcePath = sourcePath;
    }
   
    public RefreshAttribute getClone() {
        try {
            return (RefreshAttribute) clone();
        } catch (CloneNotSupportedException e) {
            Logger logger = Logger.getLogger(RefreshAttribute.class.getName());
            logger.log(Level.SEVERE, "", e);
            //cannot happen
        }
        return null;
    }
}