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
package org.apache.myfaces.scripting.core;

import org.apache.myfaces.scripting.api.ScriptingWeaver;
import org.apache.myfaces.scripting.api.ScriptingConst;
import org.apache.myfaces.scripting.core.util.ClassUtils;

import java.io.Serializable;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *
 * a dummy weaver in case the filter has not been set
 */

public class DummyWeaver implements Serializable, ScriptingWeaver {
    public void appendCustomScriptPath(String scriptPaths) {
    }

    public Object reloadScriptingInstance(Object o) {
        return o;  
    }

    public Class reloadScriptingClass(Class aclass) {
        return aclass;  
    }

    public Class loadScriptingClassFromName(String className) {
        return ClassUtils.forName(className);  
    }

    public int getScriptingEngine() {
        return ScriptingConst.ENGINE_TYPE_ALL;  
    }

    public boolean isDynamic(Class clazz) {
        return false;  
    }

    public ScriptingWeaver getWeaverInstance(Class weaverClass) {
        return this;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void fullAnnotationScan() {
    }
}
