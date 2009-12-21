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
package org.apache.myfaces.scripting.core.dependencyScan;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Label;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Set;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

class MethodScanVisitor implements MethodVisitor {

    //static final Logger log = Logger.getLogger("MethodScanVisitor");

    final Set<String> dependencies;
    final Set<String> whiteList;

    public MethodScanVisitor(Set<String> dependencies, Set<String> whiteList) {
        this.dependencies = dependencies;
        this.whiteList = whiteList;
    }


    public AnnotationVisitor visitAnnotationDefault() {
        return null;  
    }

    public AnnotationVisitor visitAnnotation(String s, boolean b) {
        return null;  
    }

    public AnnotationVisitor visitParameterAnnotation(int i, String s, boolean b) {
        return null;  
    }

    public void visitAttribute(Attribute attribute) {
        //log.log(Level.INFO, "MethodAttr {0}:", attribute.type);
        
    }

    public void visitCode() {
        //log.log(Level.INFO, "Method code");
    }

    public void visitFrame(int i, int i1, Object[] objects, int i2, Object[] objects1) {
    }

    public void visitInsn(int i) {
    }

    public void visitIntInsn(int i, int i1) {
    }

    public void visitVarInsn(int i, int i1) {
    }

    public void visitTypeInsn(int i, String castType) {
        //cast
        //log.log(Level.INFO, "TypeInsn: {0} ", new String[]{castType});
        ClassLogUtils.logParmList(dependencies, whiteList, castType);
    }

    public void visitFieldInsn(int i, String s, String s1, String s2) {
        //log.log(Level.INFO, "visitFieldInsn {0} {1} {2}", new Object[]{s, s1, s2});
        //ClassLogUtils.logParmList(dependencies, castType);
        ClassLogUtils.logParmList(dependencies, whiteList, s2);
    }

    public void visitMethodInsn(int i, String s, String s1, String s2) {
        //log.log(Level.INFO, "visitMethodIsn {0} {1} {2}", new Object[]{s, s1, s2});
        ClassLogUtils.logParmList(dependencies, whiteList, s2);
    }

    public void visitJumpInsn(int i, Label label) {

    }

    public void visitLabel(Label label) {
        
    }

    public void visitLdcInsn(Object o) {
        
    }

    public void visitIincInsn(int i, int i1) {
        
    }

    public void visitTableSwitchInsn(int i, int i1, Label label, Label[] labels) {
        
    }

    public void visitLookupSwitchInsn(Label label, int[] ints, Label[] labels) {
        
    }

    public void visitMultiANewArrayInsn(String s, int i) {
        //log.log(Level.INFO, "visitMultiANewArrayInsn: {0}", new Object[]{s});
    }

    public void visitTryCatchBlock(Label label, Label label1, Label label2, String catchType) {
        //try catch block type information in the last string
        //log.log(Level.INFO, "visitTryCatchBlock: {0} {1} {2} {3}", new Object[]{label.toString(), label1.toString(), label2.toString(), catchType});
        ClassLogUtils.logParmList(dependencies, whiteList, catchType);

    }

    public void visitLocalVariable(String s, String referenceType, String s2, Label label, Label label1, int i) {
        //local variable on method level
        //log.log(Level.INFO, "LocalVar: {0} {1} {2}", new String[]{s, referenceType, s2});
        ClassLogUtils.logParmList(dependencies, whiteList, referenceType);

    }

    public void visitLineNumber(int i, Label label) {
        
    }

    public void visitMaxs(int i, int i1) {
        
    }

    public void visitEnd() {
        
    }
}
