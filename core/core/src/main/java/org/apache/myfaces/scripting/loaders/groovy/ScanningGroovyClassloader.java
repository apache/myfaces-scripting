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
package org.apache.myfaces.scripting.loaders.groovy;

import groovy.lang.GroovyClassLoader;
import groovyjarjarasm.asm.ClassVisitor;
import groovyjarjarasm.asm.ClassWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.SourceUnit;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Scanning Groovy class loader
 * a groovy classloader which adds dependency scanning
 * as the java compiler part
 * <p/>
 * that way we can properly add artefact refreshing
 * to avoid classcast exceptions also on groovy level
 *
 * @deprecated
 */
public class ScanningGroovyClassloader extends GroovyClassLoader {
    public ScanningGroovyClassloader() {
    }

    public ScanningGroovyClassloader(ClassLoader loader) {
        super(loader);
    }

    public ScanningGroovyClassloader(GroovyClassLoader parent) {
        super(parent);
    }

    public ScanningGroovyClassloader(ClassLoader parent, CompilerConfiguration config, boolean useConfigurationClasspath) {
        super(parent, config, useConfigurationClasspath);
    }

    public ScanningGroovyClassloader(ClassLoader loader, CompilerConfiguration config) {
        super(loader, config);
    }

    /**
     * creates a ClassCollector for a new compilation.
     *
     * @param unit the compilationUnit
     * @param su   the SoruceUnit
     * @return the ClassCollector
     */
    protected ClassCollector createCollector(CompilationUnit unit, SourceUnit su) {
        InnerLoader loader = (InnerLoader) AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                return new InnerLoader(ScanningGroovyClassloader.this);
            }
        });
        return new MyClassCollector(loader, unit, su);
    }

    public static class MyClassCollector extends ClassCollector {

        public MyClassCollector(InnerLoader cl, CompilationUnit unit, SourceUnit su) {
            super(cl, unit, su);
        }

        protected Class onClassNode(ClassWriter classWriter, ClassNode classNode) {
            byte[] code = classWriter.toByteArray();

            //TODO add the scanning code here which changes our metadata and places
            //the dependencies

            return createClass(code, classNode);
        }

    }

}
