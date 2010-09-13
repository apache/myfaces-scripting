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
import org.apache.myfaces.extensions.scripting.core.util.WeavingContext;

import java.io.File;
import java.util.Collection;
import java.util.logging.Logger;

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
    volatile Class _aClass = null;
    volatile File  _sourceFile;
    volatile long  _lastLoaded = -1l;
    volatile int _scriptingEngine = ScriptingConst.ENGINE_TYPE_JSF_NO_ENGINE;

    //todo clean up the sourcepath and filename

    //--- todo move this into a separate resource handling facility

    @Override
    public String identifier() {
        return _aClass.getName();
    }

    @Override
    /**
     * returns the source file in this case
     */
    public File getFile() {
        try {
            return _sourceFile;
        } catch (NullPointerException ex) {
            return null;
        }
    }

    public void setFile(File sourceFile) {
        _sourceFile = sourceFile;
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


    public void setRefreshAttribute(RefreshAttribute attr) {
        _refreshAttribute = attr;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        ClassResource retVal = (ClassResource) super.clone();
        retVal.setRefreshAttribute(_refreshAttribute.getClone());
        return retVal;
    }


    public String getSourceFile() {
        return _sourceFile.getAbsolutePath().substring(getSourceDir().length()+1);
    }

    
    public String getSourceDir() {
        Collection<String> sourceRoots = WeavingContext.getConfiguration().getSourceDirs(_scriptingEngine);
        String fileDir = _sourceFile.getAbsolutePath();
        fileDir = fileDir.replaceAll("\\\\","/");
        for(String sourceRoot: sourceRoots) {
            sourceRoot = sourceRoot.replaceAll("\\\\","/");
            if(fileDir.startsWith(sourceRoot)) {
                return sourceRoot;
            }
        }
        return null;
    }

    public void executeLastLoaded() {
        _lastLoaded = System.currentTimeMillis();
    }

    public long getLastLoaded() {
        return _lastLoaded;
    }

    /**
     *
     * @return true if the class file has been recompiled since the last request for recompilation
     */
    public boolean isRecompiled() {
        File classFile = WeavingContext.getConfiguration().resolveClassFile(_aClass.getName());
        if(!classFile.exists()) {
            return false;
        }
        Logger log = Logger.getLogger(this.getClass().getName());
        log.info(this.getAClass().getName() + (classFile.lastModified() - _lastLoaded));

        if(_aClass.getName().contains("JavaTestComponent")) {
            System.out.println("Debugpoint found"+(classFile.lastModified() - _lastLoaded));
        }
        return _sourceFile.lastModified() > _lastLoaded;
    }


}
