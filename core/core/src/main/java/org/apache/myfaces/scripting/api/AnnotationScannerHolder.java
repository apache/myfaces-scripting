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
package org.apache.myfaces.scripting.api;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class AnnotationScannerHolder {
    Map<String, AnnotationScanner> _scannerMap = new HashMap<String, AnnotationScanner>();

    static AnnotationScannerHolder _instance = null;

    AnnotationScannerHolder() {
        super();
    }

    public static AnnotationScannerHolder getInstance() {
        if(_instance != null) {
            return _instance;
        }

        //we do not synchronize upfront for speed reasons
        synchronized(AnnotationScannerHolder.class) {
            if(_instance != null) {
                //another check just in case someone went out of synchronized
                //between the last if and synchronized
                return _instance;
            }
            _instance = new AnnotationScannerHolder();
        }

        return _instance;
    }


    public void addAnnotationScanner(String key, AnnotationScanner scanner) {
       _scannerMap.put(key, scanner);
    }

    public AnnotationScanner getAnnotationScanner(String key) {
        return _scannerMap.get(key);
    }
}
