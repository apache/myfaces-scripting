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
package org.apache.myfaces.scripting.core.classIdentifier;

import org.apache.myfaces.scripting.refresh.FileChangedDaemon;
import org.apache.myfaces.scripting.api.ScriptingWeaver;
import org.apache.myfaces.scripting.core.util.WeavingContext;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          A helper to bypass an app server startup which otherwise would be enforced
 */

public class TWeavingContext extends WeavingContext {
    public static void setWeaverForTesting(Object weaver) {
        _weaverHolder.set(weaver);
        //if (FileChangedDaemon.getInstance().getWeavers() == null) {
        //    FileChangedDaemon.getInstance().setWeavers((ScriptingWeaver) weaver);
        //}
    }

}
