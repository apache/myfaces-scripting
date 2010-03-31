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

package org.apache.myfaces.scripting.core.support;

import org.apache.myfaces.scripting.api.BaseWeaver;
import org.apache.myfaces.scripting.api.DynamicCompiler;
import org.apache.myfaces.scripting.core.util.ReflectUtil;

/**
 * This weaver does nothing except instantiating
 * the object anew at every reload instance request
 * <p/>
 * we need it to simulate the object level reloading
 * at every method call *
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class ObjectReloadingWeaver extends BaseWeaver {

    Class _clazz;

    public ObjectReloadingWeaver(Class clazz) {
        super();
        _clazz = clazz;
    }

    @Override
    public boolean isDynamic(Class clazz) {
        return true;
    }

    public void scanForAddedClasses() {
    }

    @Override
    protected DynamicCompiler instantiateCompiler() {
        return null;
    }

    @Override
    protected String getLoadingInfo(String file) {
        return null;
    }

    @Override
    public Class reloadScriptingClass(Class aclass) {
        return aclass;
    }

    @Override
    public Object reloadScriptingInstance(Object scriptingInstance, int artifactType) {
        return ReflectUtil.instantiate(_clazz);
    }
}
