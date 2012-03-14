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
package org.apache.myfaces.extensions.scripting.loaders.groovy;

import org.apache.myfaces.extensions.scripting.api.ScriptingConst;

import java.util.Map;
import java.util.HashMap;

/**
 * This class checks for reloadable class patterns
 * we do it on the java side for existing groovy objects
 *
 * @author Werner Punz
 */
public class DynamicClassIdentifier implements org.apache.myfaces.extensions.scripting.api.DynamicClassIdentifier {
    static ThreadLocal _checked = new ThreadLocal();

    public boolean isDynamic(Class clazz) {
        Map<String, Boolean> alreadyChecked = getAlreadyChecked();
        if (alreadyChecked.containsKey(clazz.getName())) {
            return alreadyChecked.get(clazz.getName());
        }

        Class[] interfaces = clazz.getInterfaces();
        for (Class anInterface : interfaces) {
            if (anInterface.getName().startsWith("groovy.lang")) {
                alreadyChecked.put(clazz.getName(), Boolean.TRUE);
                return true;
            }
        }
        alreadyChecked.put(clazz.getName(), Boolean.FALSE);
        return false;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Boolean> getAlreadyChecked() {
        Map<String, Boolean> checked = (Map<String, Boolean>) _checked.get();
        if (checked == null) {
            checked = new HashMap<String, Boolean>();
        }
        return checked;
    }

    public int getEngineType(Class clazz) {
        if (isDynamic(clazz)) {
            return ScriptingConst.ENGINE_TYPE_JSF_GROOVY;
        } else {
            return ScriptingConst.ENGINE_TYPE_JSF_NO_ENGINE;
        }
    }
}
