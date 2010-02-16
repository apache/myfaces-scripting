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
package org.apache.myfaces.scripting.refresh.sourceTracking;

import java.io.File;
import java.io.IOException;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class SourceFile {
    File _sourceFile = null;
    String _rootPath = null;

    public SourceFile(File sourceFile, String rootPath) {
        _sourceFile = sourceFile;
        _rootPath = rootPath;
    }

    public File getSourceFile() {
        return _sourceFile;
    }

    public void setSourceFile(File sourceFile) {
        _sourceFile = sourceFile;
    }

    public String getRootPath() {
        return _rootPath;
    }

    public void setRootPath(String rootPath) {
        _rootPath = rootPath;
    }

    public boolean equals(Object target) {
        if (target == null) {
            return false;
        }
        if (!(target instanceof SourceFile)) {
            return false;
        }

        if (_sourceFile == null && ((SourceFile) target).getSourceFile() == null) {
            return true;
        }
        try {
            return _sourceFile.getCanonicalPath().equals(((SourceFile) target).getSourceFile().getCanonicalPath());
        } catch (IOException e) {
            return false;
        }

    }

}
