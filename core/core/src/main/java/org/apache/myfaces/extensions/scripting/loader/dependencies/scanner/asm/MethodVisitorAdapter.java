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
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/**
 * <p>Stub implementation of the ASM MethodVisitor interface. Extend this class if you
 * only want to implement certain methods, not all of them, and don't want to bother
 * about the remaining ones. This implementation does by default nothing, i.e. if it's
 * just a callback method, it does nothing, and if it's a method that's supposed to
 * create another visitor, it returns <code>null</code>.</p>
 *
 * <p>Note that I know that there is a class called MethodAdapter in the ASM library,
 * but as far as I know this class is only a proxy, i.e. it delegates to an existing
 * implementation of the MethodVisitor interface (at least the constructor requires
 * you to pass an instance of this interface).</p>
 *
 * @author Bernhard Huemer
 */
public class MethodVisitorAdapter implements MethodVisitor {

    // ------------------------------------------ MethodVisitor methods

    public AnnotationVisitor visitAnnotationDefault() {
        return null;
    }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return null;
    }

    public AnnotationVisitor visitParameterAnnotation(int i, String s, boolean b) {
        return null;
    }

    public void visitAttribute(Attribute attribute) {

    }

    public void visitCode() {

    }

    public void visitFrame(int type, int nLocal, Object[] local,
                            int nStack, Object[] stack) {

    }

    public void visitInsn(int operationCode) {

    }

    public void visitIntInsn(int operationCode, int operand) {

    }

    public void visitVarInsn(int operationCode, int var) {
        
    }

    public void visitTypeInsn(int operationCode, String type) {

    }

    public void visitFieldInsn(int operationCode, String owner, String name, String description) {
        
    }

    public void visitMethodInsn(int operationCode, String owner, String name, String description) {

    }

    public void visitJumpInsn(int operationCode, Label label) {

    }

    public void visitLabel(Label label) {

    }

    public void visitLdcInsn(Object cst) {

    }

    public void visitIincInsn(int var, int increment) {

    }

    public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels) {

    }

    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {

    }

    public void visitMultiANewArrayInsn(String description, int dims) {

    }

    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {

    }

    public void visitLocalVariable(
            String name, String description, String signature, Label start, Label end, int index) {

    }

    public void visitLineNumber(int line, Label start) {

    }

    public void visitMaxs(int maxStack, int maxLocals) {

    }

    public void visitEnd() {

    }

}
