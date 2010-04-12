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
package org.apache.myfaces.extensions.scripting.loaders.groovy;

import org.apache.myfaces.extensions.scripting.api.ScriptingConst;
import org.apache.myfaces.extensions.scripting.api.ScriptingWeaver;
import org.apache.myfaces.extensions.scripting.core.util.WeavingContext;
import org.apache.myfaces.extensions.scripting.loaders.java.JavaDependencyScanner;
import org.apache.myfaces.extensions.scripting.loaders.java.ScannerClassloader;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Dependency scanner for groovy
 * basically the same as the java dependency scanner
 * but we use a different class here to fulfill
 * our contractual obligations with the chain
 * pattern we use for chaining different scanners
 * depending on the scripting implementation
 */
public class GroovyDependencyScanner extends JavaDependencyScanner {

    public GroovyDependencyScanner(ScriptingWeaver weaver) {
        super(weaver);
    }

    static PrivilegedExceptionAction<ScannerClassloader> CLASSLOADER_PRIVILEGED = new PrivilegedExceptionAction<ScannerClassloader>() {
        public ScannerClassloader run() {
            return new ScannerClassloader(Thread.currentThread().getContextClassLoader(), ScriptingConst.ENGINE_TYPE_JSF_GROOVY, ScriptingConst.FILE_EXTENSION_GROOVY, WeavingContext.getConfiguration().getCompileTarget());
        }
    };

    @Override
    protected ClassLoader getClassLoader() {
        //TODO move the temp dir handling into the configuration
        try {
            return AccessController.doPrivileged(CLASSLOADER_PRIVILEGED);
        } catch (PrivilegedActionException e) {
            Logger _logger = Logger.getLogger(this.getClass().getName());
            _logger.log(Level.SEVERE, "", e);
        }
        return null;
    }

    @Override
    protected int getEngineType() {
        return ScriptingConst.ENGINE_TYPE_JSF_GROOVY;
    }

}
