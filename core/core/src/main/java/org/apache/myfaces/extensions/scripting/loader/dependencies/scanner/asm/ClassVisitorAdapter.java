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
package org.apache.myfaces.extensions.scripting.loader.dependencies.scanner.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * <p>Stub implementation of the ASM ClassVisitor interface. Extend this class if you
 * only want to implement certain methods, not all of them, and don't want to bother
 * about the remaining ones. This implementation does by default nothing, i.e. if it's
 * just a callback method, it does nothing, and if it's a method that's supposed to
 * create another visitor, it returns <code>null</code>.</p>
 *
 * <p>Note that I know that there is a class called ClassAdapter in the ASM library,
 * but as far as I know this class is only a proxy, i.e. it delegates to an existing
 * implementation of the ClassVisitor interface (at least the constructor requires
 * you to pass an instance of this interface).</p>
 *
 * @author Bernhard Huemer
 */
public class ClassVisitorAdapter implements ClassVisitor {

    // ------------------------------------------ ClassVisitor methods

    public void visit(int version, int access, String name,
                      String signature, String superClassName, String[] interfaces) {
        
    }

    public void visitSource(String source, String debug) {
        
    }

    public void visitOuterClass(String owner, String name, String description) {

    }

    public AnnotationVisitor visitAnnotation(String description, boolean visible) {
        return null;
    }

    public void visitAttribute(Attribute attribute) {

    }

    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        
    }

    public FieldVisitor visitField(int access, String name, String desc,
                                    String signature, Object value) {
        return null;
    }

    public MethodVisitor visitMethod(int access, String name,
                                      String description, String signature, String[] exceptions) {
        return null;
    }

    public void visitEnd() {
        
    }
    
}
