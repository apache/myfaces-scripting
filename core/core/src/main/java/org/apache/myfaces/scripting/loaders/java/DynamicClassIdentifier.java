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
package org.apache.myfaces.scripting.loaders.java;

import org.apache.myfaces.scripting.api.ScriptingConst;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.HashMap;

/**
 * @author werpu
 *         A dynamic class identifier for java classes
 *         <p/>
 *         TODO the algorithm of this class probably is obsolete, normally
 *         a check for the classloader of the class being an instance
 *         of our custom classloader ought to be enough, this has to be tested
 *         though
 */
public class DynamicClassIdentifier implements org.apache.myfaces.scripting.api.DynamicClassIdentifier {
    static ThreadLocal _checked = new ThreadLocal();

    public boolean isDynamic(Class clazz) {
        Map<String, Boolean> alreadyChecked = getAlreadyChecked();
        if (alreadyChecked.containsKey(clazz.getName())) {
            return alreadyChecked.get(clazz.getName());
        }
        if (checkForAnnotation(clazz)) {
            alreadyChecked.put(clazz.getName(), Boolean.TRUE);
            return true;
        }

        alreadyChecked.put(clazz.getName(), Boolean.FALSE);
        return false;
    }

    private Map<String, Boolean> getAlreadyChecked() {
        Map<String, Boolean> checked = (Map<String, Boolean>) _checked.get();
        if (checked == null) {
            checked = new HashMap<String, Boolean>();
            _checked.set(checked);
        }
        return checked;
    }

    private final boolean checkForAnnotation(Class clazz) {
        //Annotation identifier = clazz.getAnnotation(ScriptingClass.class);
        if (clazz.getClassLoader() == null) return false;

        Annotation identifier = clazz.getClassLoader().getClass().getAnnotation(JavaThrowAwayClassloader.class);
        boolean annotated = identifier != null;

        return annotated;
    }

    public int getEngineType(Class clazz) {
        if (isDynamic(clazz)) {
            return ScriptingConst.ENGINE_TYPE_JSF_JAVA;
        } else {
            return ScriptingConst.ENGINE_TYPE_JSF_NO_ENGINE;
        }
    }
}
