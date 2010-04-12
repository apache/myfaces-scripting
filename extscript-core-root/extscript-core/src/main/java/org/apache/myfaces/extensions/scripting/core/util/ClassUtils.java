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
package org.apache.myfaces.extensions.scripting.core.util;

import org.apache.myfaces.shared_impl.util.ClassLoaderExtension;

import java.io.File;

/**
 * A generic utils class dealing with different aspects
 * (naming and reflection) of java classes
 *
 * @author werpu
 *         <p/>
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

    public static File classNameToFile(String classPath, String className) {
        String classFileName = classNameToRelativeFileName(className);
        return new File(classPath + File.separator + classFileName);
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

}
