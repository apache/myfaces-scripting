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

package rewrite.org.apache.myfaces.extensions.scripting.engine.dependencyScan.core;

import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureVisitor;
import rewrite.org.apache.myfaces.extensions.scripting.core.engine.dependencyScan.api.DependencyRegistry;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * We need the signature visitor to get a grip on generics
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class DependencySignatureVisitor implements SignatureVisitor {

    static final Logger _log = Logger.getLogger(DependencySignatureVisitor.class.getName());

    String _rootClass;
    String _currentClass;
    DependencyRegistry _registry;
    Integer _engineType;

    public DependencySignatureVisitor(DependencyRegistry registry, Integer engineType, String currentClass, String rootClass) {
        _registry = registry;
        _currentClass = currentClass;
        _rootClass = rootClass;
        _engineType = engineType;
    }

    public void visitFormalTypeParameter(String className) {
        if (_log.isLoggable(Level.FINEST))
            _log.log(Level.FINEST, "visitFormalTypeParameter: {0}", className);
       //the information is lacking the package information on this level no fully qualified name here 
       // _registry.addDependency(_engineType, _rootClass, _currentClass, Type.getObjectType(className).getClassName());
    }

    public SignatureVisitor visitClassBound() {
        return this;
    }

    public SignatureVisitor visitInterfaceBound() {
        return this;
    }

    public SignatureVisitor visitSuperclass() {
        return this;
    }

    public SignatureVisitor visitInterface() {
        return this;
    }

    public SignatureVisitor visitParameterType() {
        return this;
    }

    public SignatureVisitor visitReturnType() {
        return this;
    }

    public SignatureVisitor visitExceptionType() {
        return this;
    }

    public void visitBaseType(char c) {

    }

    public void visitTypeVariable(String className) {
        if (_log.isLoggable(Level.FINEST))
            _log.log(Level.FINEST, "visitTypeVariable: {0}", className);
    }

    public SignatureVisitor visitArrayType() {
        return this;
    }

    public void visitClassType(String className) {
        if (_log.isLoggable(Level.FINEST))
            _log.log(Level.FINEST, "visitClassType: {0}", className);
        _registry.addDependency(_engineType, _rootClass, _currentClass, Type.getObjectType(className).getClassName());
    }

    public void visitInnerClassType(String className) {
        if (_log.isLoggable(Level.FINEST))
            _log.log(Level.FINEST, "visitInnerClassType: {0}", className);
    }

    public void visitTypeArgument() {

    }

    public SignatureVisitor visitTypeArgument(char c) {
        return this;
    }

    public void visitEnd() {

    }
}
