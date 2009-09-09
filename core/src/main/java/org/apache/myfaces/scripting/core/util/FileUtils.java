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

import java.io.File;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class FileUtils {
    static double _tempMarker = Math.random();

    public static File getTempDir() {
        File tempDir = null;

        String baseTempPath = System.getProperty("java.io.tmpdir");
        String tempDirName = "myfaces_compilation_" + _tempMarker;

        tempDir = new File(baseTempPath + File.separator + tempDirName);
        while (tempDir.exists()) {
            tempDirName = "myfaces_compilation_" + System.currentTimeMillis() + Math.random();
            tempDir = new File(baseTempPath + File.separator + tempDirName);
        }

        synchronized (FileUtils.class) {
            if (tempDir.exists()) {
                return tempDir;
            }
            tempDir.mkdirs();
            tempDir.deleteOnExit();
        }
        return tempDir;
    }

}
