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

import org.apache.myfaces.scripting.core.util.ClassUtils;
import org.apache.myfaces.scripting.core.util.Strategy;
import org.apache.myfaces.scripting.core.util.WeavingContext;

import java.io.File;
import java.util.Set;

/**
 * A scan strategy for scanning class files within our api
 */
@SuppressWarnings("unused")
public class ClassScanStrategy implements Strategy {
    Set<String> _whiteList;
    DependencyScanner _scanner;
    String _rootDir;
    ClassLoader _loader;


    public ClassScanStrategy(ClassLoader loader, String rootDir, Set<String> whiteList, DependencyScanner scanner) {
        this._scanner = scanner;
        this._rootDir = rootDir;
        this._whiteList = whiteList;
    }

    public void apply(Object element) {
        File foundFile = (File) element;
        String fileName = foundFile.getName().toLowerCase();
        if (!fileName.endsWith(".class")) return;

        String className = ClassUtils.relativeFileToClassName(fileName.substring(_rootDir.length() + 1));
        Set<String> classDependencies = _scanner.fetchDependencies(_loader, className, _whiteList);

        WeavingContext.getFileChangedDaemon().getDependencyMap().addDependencies(className, classDependencies);
    }


}
