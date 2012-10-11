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
package org.apache.myfaces.extensions.scripting.core.engine.dependencyScan.filter;

import org.apache.myfaces.extensions.scripting.core.engine.dependencyScan.api.ClassFilter;

import java.util.Arrays;

/**
 * a filter which works on the scan identifiers
 * only classes which trigger on the same identifier
 * are allowed to be passed through
 */
public class ScanIdentifierFilter implements ClassFilter
{

    private final int [] _engineType;

    public ScanIdentifierFilter(int ... engineType) {
        _engineType = Arrays.copyOf(engineType, engineType.length);
    }

    public boolean isAllowed(Integer identifier, String clazz) {
        int id = identifier;
        for(int engineType: _engineType) {
            boolean allowed = engineType == id;
            if(allowed) return true;
        }
        return false;
    }
}
