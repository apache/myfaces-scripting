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

import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * <p/>
 * Classloader which loads the compiled files of the corresponding scripting engine
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
@JavaThrowAwayClassloader
public class RecompiledClassLoader extends ClassLoader {
    int _scriptingEngine;
    String _engineExtension;
    boolean _unTaintClasses = true;
    String _sourceRoot;
    ThrowawayClassloader _throwAwayLoader = null;

    public RecompiledClassLoader(final ClassLoader classLoader, final int scriptingEngine, final String engineExtension) {
        super(classLoader);
        _scriptingEngine = scriptingEngine;
        _engineExtension = engineExtension;
        _throwAwayLoader = AccessController.doPrivileged(new PrivilegedAction<ThrowawayClassloader>() {
            public ThrowawayClassloader run() {
                return new ThrowawayClassloader(classLoader, scriptingEngine, engineExtension);
            }
        });
    }

    public RecompiledClassLoader(ClassLoader classLoader, final int scriptingEngine, final String engineExtension, final boolean untaint) {
        this(classLoader, scriptingEngine, engineExtension);
        _unTaintClasses = untaint;
        _throwAwayLoader = AccessController.doPrivileged(new PrivilegedAction<ThrowawayClassloader>() {
            public ThrowawayClassloader run() {
                return new ThrowawayClassloader(getParent(), scriptingEngine, engineExtension, untaint);
            }
        });
    }

    RecompiledClassLoader() {
    }

    public InputStream getResourceAsStream(String name) {
        return _throwAwayLoader.getResourceAsStream(name);
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        //check if our class exists in the tempDir
        _throwAwayLoader = AccessController.doPrivileged(new PrivilegedAction<ThrowawayClassloader>() {
            public ThrowawayClassloader run() {
                return new ThrowawayClassloader(getParent(), _scriptingEngine, _engineExtension, _unTaintClasses);
            }
        });
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
