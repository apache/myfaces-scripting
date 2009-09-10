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
package org.apache.myfaces.scripting.core.util;


import org.apache.bcel.util.SyntheticRepository;
import org.apache.bcel.util.ClassPath;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ClassGen;
import org.apache.myfaces.shared_impl.util.ClassLoaderExtension;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;
import java.io.IOException;
import java.io.File;

/**
 * @author werpu
 *         <p/>
 *         A generic utils class dealing with different aspects
 *         (naming and reflection) of java classes
 */
public class ClassUtils {


    public static Class forName(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * we use the BCEL here to add a marker interface dynamically on the compiled java class
     * so that later we can identify the marked class as being of dynamic origin
     * that way we dont have to hammer any data structure but can work over introspection
     * to check for an implemented marker interface
     * <p/>
     * I cannot use the planned annotation for now
     * because the BCEL has annotation support only
     * in the trunk but in no official release,
     * the annotation support will be added as soon as it is possible to use it
     *
     * @param classPath the root classPath which hosts our class
     * @param className the className from the class which has to be rewritten
     * @throws ClassNotFoundException
     */
    public static void markAsDynamicJava(String classPath, String className) throws ClassNotFoundException {
        SyntheticRepository repo = SyntheticRepository.getInstance(new ClassPath(classPath));
        repo.clear();
        JavaClass javaClass = repo.loadClass(className);
        ClassGen classGen = new ClassGen(javaClass);

        classGen.addInterface("org.apache.myfaces.scripting.loaders.java._ScriptingClass");
        classGen.update();

        File target = classNameToFile(classPath, className);

        try {
            classGen.getJavaClass().dump(target);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static File classNameToFile(String classPath, String className) {
        String classFileName = classNameToRelativeFileName(className);
        File target = new File(classPath + File.separator + classFileName);
        return target;
    }

    private static String classNameToRelativeFileName(String className) {
        return className.replaceAll("\\.", File.separator) + ".class";
    }

    public static String relativeFileToClassName(String relativeFileName) {
        String className = relativeFileName.replaceAll("\\\\", ".").replaceAll("\\/", ".");
        className = className.substring(0, className.lastIndexOf("."));
        return className;
    }

    public static ClassLoader getContextClassLoader() {
        return org.apache.myfaces.shared_impl.util.ClassUtils.getContextClassLoader();
    }

    public static void addClassLoadingExtension(ClassLoaderExtension extension, boolean top) {
        org.apache.myfaces.shared_impl.util.ClassUtils.addClassLoadingExtension(extension, top);
    }

    public Class classForName(String name) throws ClassNotFoundException {
        return org.apache.myfaces.shared_impl.util.ClassUtils.classForName(name);
    }
}
