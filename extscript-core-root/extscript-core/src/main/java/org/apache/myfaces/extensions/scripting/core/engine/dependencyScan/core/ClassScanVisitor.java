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
package org.apache.myfaces.extensions.scripting.core.engine.dependencyScan.core;

import org.apache.myfaces.extensions.scripting.core.engine.dependencyScan.api.DependencyRegistry;
import org.apache.myfaces.extensions.scripting.core.engine.dependencyScan.registry.ExternalFilterDependencyRegistry;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The central visitor for the class scanner. ASM uses a visitor interface for high performance
 * to step through classes.
 * <p>&nbsp;</p>
 * We reuse this pattern to get the best performance possible in this critical part of the application
 * which also is triggered by the startup process.
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public class ClassScanVisitor implements ClassVisitor {

    DependencyRegistry _dependencyRegistry;
    String _currentlyVistedClass;
    Integer _engineType;
    String _rootClass;
    static final Logger _log = Logger.getLogger(ClassScanVisitor.class.getName());

    public ClassScanVisitor() {
    }

    public ClassScanVisitor(Integer engineType, String rootClass, ExternalFilterDependencyRegistry registry) {
        _dependencyRegistry = registry;
        _engineType = engineType;
        _rootClass = rootClass;

    }

    public void visit(int version, int access, String name,
                      String signature, String superName, String[] interfaces) {
        _currentlyVistedClass = Type.getObjectType(name).getClassName();
        if (superName != null)
            registerDependency(Type.getObjectType(superName));

        handleGenerics(signature, true);

        if (interfaces != null && interfaces.length > 0) {
            for (String currInterface : interfaces) {
                if (currInterface != null)
                    registerDependency(Type.getObjectType(currInterface));
            }
        }
    }

    public void visitSource(String source, String debug) {
        _log.log(Level.FINEST, "visitSource: {0}", source);
    }

    public void visitOuterClass(String owner, String name, String description) {
        //nothing has to be done here I guess because
        //we only try to fetch the dependencies
        _log.log(Level.FINEST, "visitOuterClass: {0} {1} {2}", new String[]{owner, name, description});

    }

    public AnnotationVisitor visitAnnotation(String description,
                                             boolean visible) {
        if (description != null)
            registerDependency(Type.getType(description));

        return null;
    }

    public void visitAttribute(Attribute attribute) {
    }

    public void visitInnerClass(String name, String outerName,
                                String innerName, int access) {
        //same as outer class
        _log.log(Level.FINEST, "visitInnerClass: {0}  {1} {2} ", new String[]{name, outerName, innerName});
    }

    public FieldVisitor visitField(int access, String name, String description,
                                   String signature, Object value) {
        //_log._log(Level.INFO, "Field:{0} {1} ", new Object[]{description, name});
        handleGenerics(signature, false);
        if (description != null)
            registerDependency(Type.getType(description));

        return null;
    }

    private void registerDependency(Type dependency) {
        String className = dependency.getClassName();
        if (className.endsWith("[]")) {
            className = className.substring(0, className.indexOf("["));
        }

        if (_dependencyRegistry != null) {
            _dependencyRegistry.addDependency(_engineType, _rootClass, _currentlyVistedClass, className);
        }

    }

    public MethodVisitor visitMethod(int access, String name,
                                     String description, String signature, String[] exceptions) {

        if (description != null)
            registerDependency(Type.getReturnType(description));

        handleGenerics(signature, true);

        if (description != null) {
            for (Type argumentType : Type.getArgumentTypes(description)) {
                registerDependency(argumentType);
            }
        }
        return new MethodScanVisitor(_engineType, _rootClass, _currentlyVistedClass, _dependencyRegistry);
    }

    private void handleGenerics(String signature, boolean accept) {
        if (signature != null && signature.contains("<")) {
            SignatureReader reader = new SignatureReader(signature);
            if (accept)
                reader.accept(new DependencySignatureVisitor(_dependencyRegistry, _engineType, _rootClass, _currentlyVistedClass));
            else
                reader.acceptType(new DependencySignatureVisitor(_dependencyRegistry, _engineType, _rootClass, _currentlyVistedClass));
        }
    }

    public void visitEnd() {
        //_log.info("}");
    }

    public void setDependencyRegistry(DependencyRegistry dependencyRegistry) {
        _dependencyRegistry = dependencyRegistry;
    }

    public void setEngineType(Integer engineType) {
        _engineType = engineType;
    }

    public void setRootClass(String rootClass) {
        _rootClass = rootClass;
    }
}


