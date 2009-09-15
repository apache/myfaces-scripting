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
package org.apache.myfaces.scripting.jsf2.annotation;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.Type;

import java.util.List;
import java.util.LinkedList;
import java.io.File;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          Source path annotation scanner for java it scans all sources in the specified source paths
 *          recursively for additional information
 *          and then adds the id/name -> class binding information to the correct factory locations,
 *          wherever possible
 */

public class SourceAnnotationScanner {

    List<SourceClassAnnotationListener> _listeners = new LinkedList<SourceClassAnnotationListener>();
    JavaDocBuilder _builder = new JavaDocBuilder();

    public SourceAnnotationScanner(String... sourcePaths) {

        for (String sourcePath : sourcePaths) {
            File sourcePathFile = new File(sourcePath);
            if (sourcePathFile.exists()) {
                _builder.addSourceTree(sourcePathFile);
            }
        }

        _listeners.add(new BeanImplementationListener());

    }


    /**
     * builds up the parsing chain and then notifies its observers
     * on the found data
     */
    public void doiIt() {
        JavaSource []sources = _builder.getSources();
        for(JavaSource source: sources) {
            String packageName = source.getPackage();
            JavaClass[] classes = source.getClasses();
            for(JavaClass clazz: classes) {
                Type[] annotations = clazz.getImplements();
                if(clazz.getName().contains("Bean2")) {
                    //todo to reverse engineer the missing
                    //  annotation detection docs of qdox
                    System.out.println("debugpoint found");
                }
            }
        }


    }


}
