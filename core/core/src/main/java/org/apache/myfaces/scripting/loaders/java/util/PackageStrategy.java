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
package org.apache.myfaces.scripting.loaders.java.util;

import org.apache.commons.io.FilenameUtils;
import org.apache.myfaces.scripting.core.util.Strategy;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          Java strategy pattern to filter out all fully qualified classes
 *          under a given dir (excluding the inner classes)
 */

public class PackageStrategy
        implements Strategy

{
    String rootDir = "";
    Set<String> _foundFiles;
    Pattern rePattern;


    public PackageStrategy(Set<String> target, String pattern) {
        pattern = pattern.trim().replaceAll("\\.", "\\\\.");
        pattern = "." + pattern;

        _foundFiles = target;

        rePattern = Pattern.compile(pattern);

    }

    //only directories with classes in it apply to be valid
    //namespaces!

    public void apply(Object element) {
        File foundFile = (File) element;
        String fileName = foundFile.getName().toLowerCase();
        Matcher matcher = rePattern.matcher(fileName);

        if (!matcher.matches()) return;

        if (!foundFile.isDirectory()) {
            String relativePath = foundFile.getPath().substring(rootDir.length() + 1);
            relativePath = FilenameUtils.separatorsToUnix(relativePath);
            _foundFiles.add(relativePath.replaceAll("\\/", "."));
        }
    }

    public Set<String> getFoundFiles() {
        return _foundFiles;
    }

    public void setFoundFiles(Set<String> foundFiles) {
        _foundFiles = foundFiles;
    }
}
