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
package org.apache.myfaces.extensions.scripting.cdi.loaders.dependency.scanner.asm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.extensions.scripting.cdi.loaders.dependency.registry.DependencyRegistry;
import org.apache.myfaces.extensions.scripting.cdi.loaders.dependency.scanner.DependencyScanner;
import org.objectweb.asm.*;

import java.io.IOException;
import java.io.InputStream;

/**
 * <p>A dependency scanner that uses the ASM library to read class files. In the course of this
 * process it determines the dependencies of a class and stores them in a given dependency
 * registry.</p>
 *
 * @author Bernhard Huemer
 */
public class AsmClassReadingDependencyScanner implements DependencyScanner {

    /** The logger instance for this class. */
    private static final Log logger = LogFactory.getLog(AsmClassReadingDependencyScanner.class);

    // ------------------------------------------ DependencyScanner methods

    public void scan(DependencyRegistry registry, ClassLoader classLoader, String className) {
        try {
            InputStream classStream =
                    classLoader.getResourceAsStream(className.replace('.', '/') + ".class");
            ClassReader reader = new ClassReader(classStream);
            reader.accept(new ClassScanner(registry, className), 0);
        } catch (IOException ex) {
            logger.error("An I/O error occurred while scanning the dependencies of the class '"
                    + className + "' using the class loader '" + classLoader + "'.", ex);
        }
    }

    // ------------------------------------------ Utility methods

    /**
     * <p>Utility methods that registers a dependency on the given type.</p>
     *
     * @param registry the registry to use for storing dependencies
     * @param className the name of the class you're scanning for dependencies
     * @param dependencyClass the dependency you want to register
     * @param declaration additional information about where the dependency has been declared
     *                      (only required for logging purposes)
     */
    private void registerDependency(
            DependencyRegistry registry, String className, String dependencyClass, String declaration) {
        registry.registerDependency(className, dependencyClass);

        if (logger.isTraceEnabled()) {
            logger.trace("Due to '" + declaration + "' the class '" + className
                    + "' introduces a dependency on the class '" + dependencyClass + "'.");
        }
    }

    /**
     * <p>Utility methods that registers a dependency on the given type if appropriate,
     * i.e. only if it's even a class type. Otherwise the dependency will be ignored.</p>
     *
     * @param registry the registry to use for storing dependencies
     * @param className the name of the class you're scanning for dependencies
     * @param dependency the dependency you want to register
     * @param declaration additional information about where the dependency has been declared
     *                      (only required for logging purposes)
     */
    private void registerDependency(
            DependencyRegistry registry, String className, Type dependency, String declaration) {
        if (dependency.getSort() == Type.OBJECT) {
            registry.registerDependency(className, dependency.getClassName());

            if (logger.isTraceEnabled()) {
                logger.trace("Due to '" + declaration + "' the class '" + className
                        + "' introduces a dependency on the class '" + dependency.getClassName() + "'.");
            }
        } else {
            if (logger.isTraceEnabled()) {
                logger.trace("Due to '" + declaration + "' the class '" + className + "' introduces a dependency on the type '"
                        + dependency.getClassName() + "', but as it's not even a class, it will be ignored.");
            }
        }
    }

    /**
     * <p>Converts a given internal name of a class into the usual representation with
     * dots in between.</p>
     *
     * @param internalName the internal name of a class, i.e. the one with those slashes
     *
     * @return the usual class name of that given class
     */
    private String convertInternalNameToClassName(String internalName) {
        return internalName.replace('/', '.');
    }

    // ------------------------------------------ Private classes

    /**
     * <p>This class scans the contents of a class and determines
     * all the dependencies of that class in doing so.</p>
     *
     */
    private class ClassScanner extends ClassVisitorAdapter {

        /** The registry to use for storing dependencies */
        private DependencyRegistry registry;

        /** The name of the class we're scanning for dependencies */
        private String className;

        // -------------------------------------- Constructors

        public ClassScanner(DependencyRegistry registry, String className) {
            this.registry = registry;
            this.className = className;
        }

        // -------------------------------------- ClassVisitor methods

        /**
         * <p>Callback method that will be called once the class reader starts scanning a new class.</p>
         */
        @Override
        public void visit(int version, int access, String name, String signature,
                            String superClassName, String[] interfaces) {
            registerDependency(registry, className,
                    convertInternalNameToClassName(superClassName), "'Parent class'");
        }

        /**
         * <p>Callback method that will be called once the class reader encounters an annotation.</p>
         *
         * @param description the class descriptor of the annotation class.
         * @param visible <code>true</code> if the annotation is visible at runtime
         *
         * @return <code>null</code> as we don't need to actually "visit" the annotation
         */
        @Override
        public AnnotationVisitor visitAnnotation(String description, boolean visible) {
            if (visible) {
                registerDependency(registry, className, Type.getType(description), "'Class annotation'");
            }

            // No further investigation required as we don't have to
            // actually scan dependencies transitively.
            return null;
        }

        /**
         * <p>Callback method that will be called once the class reader encounters a field.</p>
         *
         * @param access the field's access flags
         * @param name the field's name
         * @param description the field's descriptor (see Type)
         * @param signature the field's signature
         * @param value the field's initial value
         *
         * @return <code>null</code> as we don't need to actually "visit" the field
         */
        @Override
        public FieldVisitor visitField(int access, String name, String description, String signature, Object value) {
            registerDependency(registry, className,
                    Type.getType(description), "'Type of the field [" + name + "]'");

            // No further investigation required.
            return null;
        }

        /**
         * <p>Callback method that will be called once the class reader encounters a method.</p>
         *
         * @return a method scanner that tries to scan more dependencies in the body of the method
         */
        @Override
        public MethodVisitor visitMethod(int access, String methodName, String description,
                                            String signature, String[] exceptions) {
            // Register all dependencies that the method signature itself introduces ..
            registerDependency(registry, className,
                    Type.getReturnType(description), "'Return type of the method [" + methodName + "]'");
            for (Type argumentType : Type.getArgumentTypes(description)) {
                registerDependency(registry, className,
                        argumentType, "'Argument type of the method [" + methodName + "]'");
            }

            // .. and then continue scanning the method body.
            return new MethodScanner(registry, className, methodName);
        }
    }

    /**
     * <p>This class scans the contents of a method and determines
     * all the dependencies of that method in doing so.</p>
     *
     */
    private class MethodScanner extends MethodVisitorAdapter {

        /** The registry to use for storing dependencies */
        private DependencyRegistry registry;

        /** The name of the class we're scanning for dependencies */
        private String className;

        /** The name of the method we're scanning for dependencies */
        private String methodName;

        // -------------------------------------- Constructors

        public MethodScanner(DependencyRegistry registry, String className, String methodName) {
            this.registry = registry;
            this.className = className;
            this.methodName = methodName;
        }

        // -------------------------------------- MethodVisitor methods

        /**
         * <p>Callback method that will be called once the class reader
         * encounters a local variable within a method.</p>
         *
         */
        @Override
        public void visitLocalVariable(String name, String description,
                                        String signature, Label start, Label end, int index) {
            registerDependency(registry, className, Type.getType(description),
                    "'Local variable [" + name + "] in the method [" + methodName + "]");
        }

        /**
         * <p>Callback method that will be called once the class reader encounters
         * a method instruction, i.e. an instruction that invokes a method.</p>
         *
         */
        @Override
        public void visitMethodInsn(int operationCode, String owner, String name, String description) {
            String ownerClass = convertInternalNameToClassName(owner);
            if (!className.equals(ownerClass)) {
                registerDependency(registry, className, ownerClass,
                        "'Calling the method [" + name + "] within the method [" + methodName + "]'");
            }
        }

        /**
         * <p>Callback method that will be called once the class reader encounters
         * a type instruction, i.e. either a new statement, a cast or an instanceof
         * check.</p>
         *
         */
        @Override
        public void visitTypeInsn(int operationCode, String type) {
            String typeInstructionClassName = convertInternalNameToClassName(type);
            registerDependency(registry, className, typeInstructionClassName, "'Type instruction within the method ["
                    + methodName + "], i.e. either a cast, a new operation, or an instanceof check'");
        }

        /**
         * <p>Callback method that will be called on the class reader encounters
         * a field instruction, i.e. an instruction that accesses e.g. a static
         * field of another class.</p>
         *
         */
        @Override
        public void visitFieldInsn(int operationCode, String owner, String name, String description) {
            String ownerClass = convertInternalNameToClassName(owner);
            if (!className.equals(ownerClass)) {
                registerDependency(registry, className, ownerClass,
                        "'Accessing the field [" + name + "] within the method [" + methodName + "]'");
            }
        }

        /**
         * <p>Callback method that will be called once the class reader
         * encounters a try-catch block within a method.</p>
         *
         */
        @Override
        public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
            if (type != null) { // If we're not dealing with a "finally" block
                registerDependency(registry, className,
                        Type.getType(type), "'TryCatch block in the method [" + methodName + "]'");
            }
        }
    }

}