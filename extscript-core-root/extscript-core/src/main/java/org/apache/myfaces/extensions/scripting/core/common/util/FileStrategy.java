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
package org.apache.myfaces.extensions.scripting.core.common.util;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          Java file strategy pattern to filter out all java files which are possible sources
 *          so that we can recompile them
 */

public class FileStrategy implements Strategy
{
    Pattern _rePattern;

    public FileStrategy(String pattern) {
        pattern = pattern.trim().replaceAll("\\.", "\\\\.");
        pattern = "." + pattern;

        _rePattern = Pattern.compile(pattern);

    }

    public FileStrategy(Pattern pattern) {


            _rePattern = pattern;

        }


    List<File> _foundFiles = new LinkedList<File>();

    public void apply(Object element) {
        File foundFile = (File) element;
        String fileName = foundFile.getName().toLowerCase(Locale.getDefault());
        Matcher matcher = _rePattern.matcher(fileName);

        if (!matcher.matches()) return;
        _foundFiles.add(foundFile);
    }

    public List<File> getFoundFiles() {
        return _foundFiles;
    }

}
