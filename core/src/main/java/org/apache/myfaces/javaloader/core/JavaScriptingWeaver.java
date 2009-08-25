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
package org.apache.myfaces.javaloader.core;

import org.apache.myfaces.scripting.api.ScriptingWeaver;

/**
 * @author werpu
 *
 * The Scripting Weaver for the java core which reloads the java scripts
 * dynamically upon change
 */
public class JavaScriptingWeaver implements ScriptingWeaver {
    public void appendCustomScriptPath(String scriptPaths) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object reloadScriptingInstance(Object o) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Class reloadScriptingClass(Class aclass) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Class loadScriptingClassFromName(String className) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
