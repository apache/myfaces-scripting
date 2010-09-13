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
package org.apache.myfaces.extensions.scripting.core.classIdentifier;

import org.apache.myfaces.extensions.scripting.core.support.Consts;
import org.apache.myfaces.extensions.scripting.sandbox.loader.support.ThrowAwayClassLoader;
import org.apache.myfaces.extensions.scripting.core.util.ClassUtils;
import org.apache.myfaces.extensions.scripting.loaders.java.JavaThrowAwayClassloader;

import java.io.File;
import java.io.FileInputStream;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          A classloader implementing our throwaway classloader interface
 *          the new detection algorithm depends on the classloader
 *          instead of runtime alteration of the classes to attach
 *          an interface
 */
@JavaThrowAwayClassloader
public class DynamicClassloader extends ClassLoader implements ThrowAwayClassLoader {
    String _rootPath = "";

    public DynamicClassloader(ClassLoader classLoader, String rootPath) {
        super(classLoader);
        _rootPath = rootPath;
    }

    public Class loadClass(String className, boolean resolve) throws ClassNotFoundException {

        if(className.contains("rg/apache/myfaces/javaloader/componentTest/JavaTestComponent$PropertyKeys")) {
            System.out.println("Debuginfo found");
        }

        if (className.contains(Consts.JAVA_LANG)) {
            return super.loadClass(className, resolve);
        }

        File classFile = ClassUtils.classNameToFile(_rootPath, className);
        assertClassfile(className, classFile);

        FileInputStream iStream = null;
        int fileLength = (int) classFile.length();
        byte[] fileContent = new byte[fileLength];

        try {
            iStream = new FileInputStream(classFile);
            iStream.read(fileContent);

            //we have to do it here because just in case
            //a dependend class is loaded as well we run into classcast exceptions
            return super.defineClass(className, fileContent, 0, fileLength);

        } catch (Exception e) {
            throw new ClassNotFoundException(e.toString());
        } finally {
            if (iStream != null) {
                try {
                    iStream.close();
                } catch (Exception e) {
                }
            }
        }
    }

    private void assertClassfile(String className, File classFile) throws ClassNotFoundException {
        if (!classFile.exists()) {
            throw new ClassNotFoundException(className + " not found");
        }
    }

    public boolean isOutdated(long lastModified) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
