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


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.shared_impl.util.ClassLoaderExtension;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


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

    public static boolean isPresent(String clazz) {
        try {
            getContextClassLoader().loadClass(clazz);
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }


    /**
     * We use asm here to add the marker annotation
     * to the list of our public annotations
     *
     * @param classPath the root classPath which hosts our class
     * @param className the className from the class which has to be rewritten
     * @throws ClassNotFoundException
     */
    /*   public static void markAsDynamicJava(String classPath, String className) throws ClassNotFoundException {
    FileInputStream fIstr = null;
    FileOutputStream foStream = null;
    try {
        File classFile = classNameToFile(classPath, className);
        fIstr = new FileInputStream(classFile);

        ClassNode node = new ClassNode();
        ClassReader clsReader = new ClassReader(fIstr);
        //ClassWriter wrt = new ClassWriter();
        clsReader.accept((ClassVisitor) node, ClassReader.SKIP_FRAMES);
        //node.accept(wrt);
        ClassWriter wrt = new ClassWriter(0);

        if (node.visibleAnnotations == null) {
            node.visibleAnnotations = new LinkedList<AnnotationNode>();
        }

        boolean hasAnnotation = false;
        String annotationMarker = Type.getDescriptor(ScriptingClass.class);
        for (Object elem : node.visibleAnnotations) {
            AnnotationNode aNode = (AnnotationNode) elem;
            if (aNode.desc.equals(annotationMarker)) {
                hasAnnotation = true;
                break;
            }
        }
        if (!hasAnnotation) {
            node.visibleAnnotations.add(new AnnotationNode(annotationMarker));
        }
        node.accept(wrt);

        byte[] finalClass = wrt.toByteArray();
        fIstr.close();
        fIstr = null;

        foStream = new FileOutputStream(classNameToFile(classPath, className));
        foStream.write(finalClass);
        foStream.flush();

    } catch (FileNotFoundException ex) {
        throw new ClassNotFoundException("Class " + className + " not found ");

    } catch (IOException e) {
        logError(e);
    } finally {
        closeStreams(fIstr, foStream);
    }

}    */
    private static void logError(IOException e) {
        Log log = LogFactory.getLog(ClassUtils.class);
        log.error(e);
    }

    private static void closeStreams(FileInputStream fIstr, FileOutputStream foStream) {
        try {
            if (fIstr != null) {
                fIstr.close();
            }
        } catch (IOException e) {
            logError(e);
        }
        try {
            if (foStream != null) {
                foStream.close();
            }
        } catch (IOException e) {
            logError(e);
        }
    }


    public static File classNameToFile(String classPath, String className) {
        String classFileName = classNameToRelativeFileName(className);
        File target = new File(classPath + File.separator + classFileName);
        return target;
    }

    private static String classNameToRelativeFileName(String className) {
        String separator = FileUtils.getFileSeparatorForRegex();

        return className.replaceAll("\\.", separator) + ".class";
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
