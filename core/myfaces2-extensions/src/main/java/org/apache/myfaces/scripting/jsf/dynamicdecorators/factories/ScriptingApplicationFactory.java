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
package org.apache.myfaces.scripting.jsf.dynamicdecorators.factories;

import org.apache.myfaces.scripting.jsf.dynamicdecorators.implemetations.ApplicationProxy;
import org.apache.myfaces.scripting.api.Decorated;
import org.apache.myfaces.scripting.core.util.ProxyUtils;

import javax.faces.application.ApplicationFactory;
import javax.faces.application.Application;


/**
 * Application factory which introduces
 * scripting proxies for their artefacts
 *
 * We use a mix of AOP and helper constructs
 * to reach the goal to be dynamic.
 * For most artefacts we just need to
 * check if the object is a Groovy object
 * and then reload at their connection interfaces
 *
 * Some artefacts have a longer lifespan and/or are stateless
 * for those we have to work with reloading AOP 
 *
 *
 * @author Werner Punz
 */
public class ScriptingApplicationFactory extends ApplicationFactory implements Decorated {

    ApplicationFactory _delegate;
    boolean scriptingEnabled = false;

    public ScriptingApplicationFactory(ApplicationFactory delegate) {
        _delegate = delegate;
        scriptingEnabled = ProxyUtils.isScriptingEnabled();
    }

    public Application getApplication() {
        Application retVal = _delegate.getApplication();  //To change body of implemented methods use File | Settings | File Templates.

        
        if (scriptingEnabled && !(retVal instanceof ApplicationProxy) )
            retVal = new ApplicationProxy(retVal);

        return retVal;
    }

    public void setApplication(Application application) {
        if (scriptingEnabled && !(application instanceof ApplicationProxy))
            application = new ApplicationProxy(application);

        _delegate.setApplication(application);
    }

    public Object getDelegate() {
        return _delegate;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
