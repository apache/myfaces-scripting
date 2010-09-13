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
package org.apache.myfaces.extensions.scripting.loaders.java;

import org.apache.myfaces.extensions.scripting.core.util.WeavingContext;
import org.apache.myfaces.extensions.scripting.monitor.ClassResource;

import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    static class _Action implements PrivilegedExceptionAction<ThrowawayClassloader> {

        ClassLoader _parent;
        Integer _scriptingEngine;
        String _engineExtension;
        Boolean _untaint;

        _Action(ClassLoader parent, Integer scriptingEngine, String engineExtension, Boolean untaint) {
            _parent = parent;
            this._scriptingEngine = scriptingEngine;
            this._engineExtension = engineExtension;
            this._untaint = untaint;
        }

        _Action(ClassLoader parent, Integer scriptingEngine, String engineExtension) {
            _parent = parent;
            _scriptingEngine = scriptingEngine;
            _engineExtension = engineExtension;
            _untaint = null;
        }

        public ThrowawayClassloader run() {
            if (_untaint != null)
                return new ThrowawayClassloader(_parent, _scriptingEngine, _engineExtension, _untaint);
            else
                return new ThrowawayClassloader(_parent, _scriptingEngine, _engineExtension);
        }
    }

    RecompiledClassLoader() {
    }
    

    public RecompiledClassLoader(final ClassLoader classLoader, final int scriptingEngine, final String engineExtension) {
        super(classLoader);
        _scriptingEngine = scriptingEngine;
        _engineExtension = engineExtension;
        try {
            _throwAwayLoader = AccessController.doPrivileged(new _Action(classLoader, scriptingEngine, engineExtension));
        } catch (PrivilegedActionException e) {
            logSevere(e);
        }
    }

    public RecompiledClassLoader(ClassLoader classLoader, final int scriptingEngine, final String engineExtension, final boolean untaint) {
        this(classLoader, scriptingEngine, engineExtension);
        _unTaintClasses = untaint;
        final ClassLoader _parent = getParent();
        try {
            _throwAwayLoader = AccessController.doPrivileged(
                    new _Action(_parent, scriptingEngine, engineExtension, untaint)
            );
        } catch (PrivilegedActionException e) {
            logSevere(e);
        }
    }

    private void logSevere(PrivilegedActionException e) {
        Logger _logger = Logger.getLogger(this.getClass().getName());
        _logger.log(Level.SEVERE, "", e);
    }


    public InputStream getResourceAsStream(String name) {
        return _throwAwayLoader.getResourceAsStream(name);
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        //check if our class exists in the tempDir
        final ClassLoader _parent = getParent();
        if(className.contains("JavaTestComponent")) {
            System.out.println("Debugpoint found");
        }
        ClassResource resource = WeavingContext.getFileChangedDaemon().getClassMap().get(className);
        
        //preemptive check if the resource either is not loaded or requires a refresh
        //if yes we generated a new classloader to load the class anew
        if(resource == null || resource.getAClass() == null || resource.isRecompiled())  {
            return _loadClass(className, _parent);
        }
        return resource.getAClass();
    }

    private Class<?> _loadClass(String className, ClassLoader _parent) throws ClassNotFoundException {
        try {
            _throwAwayLoader = AccessController.doPrivileged(
                    new _Action(_parent, _scriptingEngine, _engineExtension, _unTaintClasses)
            );
            return _throwAwayLoader.loadClass(className);
        } catch (PrivilegedActionException e) {
            logSevere(e);
            return null;
        }
    }


    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return _throwAwayLoader.findClassExposed(name);
    }

    @SuppressWarnings("unused")
    public String getSourceRoot() {
        return _sourceRoot;
    }

    public void setSourceRoot(String sourceRoot) {
        this._sourceRoot = sourceRoot;
    }
}
