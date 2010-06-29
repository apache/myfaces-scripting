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

import java.io.File;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class ClassResource extends WatchedResource {
    /*
    * volatile due to the ram concurrency behavior
    * of the instance vars jdk 5+
    */

    //TODO we probably can drop the file definitions
    //the class has all meta data internally via findResource
    //on its corresponding classloader
    //caching the info however probably is faster
    volatile String _fileName = "";
    volatile String _sourcePath = "";
    volatile Class _aClass = null;
    volatile int _scriptingEngine = ScriptingConst.ENGINE_TYPE_JSF_NO_ENGINE;


    

    //todo clean up the sourcepath and filename
    
    //--- todo move this into a separate resource handling facility

    @Override
    public String identifier() {
        return _aClass.getName();
    }

    @Override
    public File getFile() {
        //todo get the resource file from the given class definition and its resource loader
        return new File(_sourcePath+File.separator+_fileName);
    }

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



    public void setRefreshAttribute(RefreshAttribute attr) {
        _refreshAttribute = attr;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        ClassResource retVal = (ClassResource) super.clone();
        retVal.setRefreshAttribute(_refreshAttribute.getClone());        
        return retVal;
    }
}
