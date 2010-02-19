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
import org.apache.myfaces.scripting.api.ScriptingConst;
import org.apache.myfaces.scripting.core.util.ClassUtils;
import org.apache.myfaces.scripting.core.util.FileUtils;
import org.apache.myfaces.scripting.core.util.WeavingContext;
import org.apache.myfaces.scripting.refresh.RefreshContext;
import org.apache.myfaces.scripting.refresh.ReloadingMetadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.Collection;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          Classloader which loads the compilates for the scripting engine
 */
@JavaThrowAwayClassloader
public class RecompiledClassLoader extends ClassLoader {
    int _scriptingEngine;
    String _engineExtension;
    boolean _unTaintClasses = true;
    String _sourceRoot;
    RecompiledClassLoaderInternal _throwAwayLoader = null;

    public RecompiledClassLoader(ClassLoader classLoader, int scriptingEngine, String engineExtension) {
        super(classLoader);
        _scriptingEngine = scriptingEngine;
        _engineExtension = engineExtension;
        _throwAwayLoader = new RecompiledClassLoaderInternal(classLoader, scriptingEngine, engineExtension);
    }

    public RecompiledClassLoader(ClassLoader classLoader, int scriptingEngine, String engineExtension, boolean untaint) {
        this(classLoader, scriptingEngine, engineExtension);
        _unTaintClasses = untaint;
        _throwAwayLoader = new RecompiledClassLoaderInternal(getParent(), scriptingEngine, engineExtension, untaint);
    }

    RecompiledClassLoader() {
    }

    public InputStream getResourceAsStream(String name) {
        return _throwAwayLoader.getResourceAsStream(name);
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        //check if our class exists in the tempDir
        _throwAwayLoader = new RecompiledClassLoaderInternal(getParent(), _scriptingEngine, _engineExtension, _unTaintClasses);
        _throwAwayLoader.setSourceRoot(getSourceRoot());
        return _throwAwayLoader.loadClass(className);
    }





    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return _throwAwayLoader.findClass(name);
    }


    public String getSourceRoot() {
        return _sourceRoot;
    }

    public void setSourceRoot(String sourceRoot) {
        this._sourceRoot = sourceRoot;
    }
}
