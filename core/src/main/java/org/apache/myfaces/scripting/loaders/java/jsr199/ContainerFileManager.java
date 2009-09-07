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
package org.apache.myfaces.scripting.loaders.java.jsr199;

import javax.tools.*;
import java.io.IOException;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.net.URLClassLoader;
import java.net.URL;

import org.apache.myfaces.shared_impl.util.ClassUtils;


/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class ContainerFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {

    StandardJavaFileManager _delegate = null;
    String _classPath = null;


    protected ContainerFileManager(StandardJavaFileManager standardJavaFileManager) {
        super(standardJavaFileManager);
        _delegate = standardJavaFileManager;
    }


    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String s, JavaFileObject.Kind kind, FileObject fileObject) throws IOException {
        return super.getJavaFileForOutput(location, s, kind, fileObject);
    }

    @Override
    public ClassLoader getClassLoader(Location location) {
        return ClassUtils.getContextClassLoader();
    }

    Iterable<? extends JavaFileObject> getJavaFileObjects(File... files) {
        return _delegate.getJavaFileObjects(files);
    }

    Iterable<? extends JavaFileObject> getJavaFileObjects(String... files) {
        return _delegate.getJavaFileObjects(files);
    }

    String getClassPath() {
        if (_classPath != null) {
            return _classPath;
        }
        ClassLoader cls = getClassLoader(null);
        while (!(cls instanceof URLClassLoader) && cls != null) {
            cls = cls.getParent();
        }
        if (cls == null) {
            return "";
        }

        URL[] urls = ((URLClassLoader) cls).getURLs();
        int len = urls.length;
        if (len == 0) {
            return "";
        }
        StringBuilder retVal = new StringBuilder(len * 16);

        for (int cnt = 0; cnt < len; cnt++) {
            retVal.append(urls[cnt].getFile());
            if (cnt < len - 1) {
                retVal.append(File.pathSeparator);
            }
        }
        return (_classPath = retVal.toString());
    }

}


