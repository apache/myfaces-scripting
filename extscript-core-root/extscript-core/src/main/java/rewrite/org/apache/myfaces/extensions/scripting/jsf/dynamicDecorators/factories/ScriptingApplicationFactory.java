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
package rewrite.org.apache.myfaces.extensions.scripting.jsf.dynamicdecorators.factories;

import rewrite.org.apache.myfaces.extensions.scripting.core.api.Decorated;
import rewrite.org.apache.myfaces.extensions.scripting.core.context.WeavingContext;
import rewrite.org.apache.myfaces.extensions.scripting.jsf.dynamicdecorators.implementations.ApplicationProxy;

import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;

/**
 * Application factory which introduces
 * scripting proxies for their artefacts
 * <p/>
 * We use a mix of AOP and helper constructs
 * to reach the goal to be dynamic.
 * For most artefacts we just need to
 * check if the object is a Groovy object
 * and then reload at their connection interfaces
 * <p/>
 * Some artefacts have a longer lifespan and/or are stateless
 * for those we have to work with reloading AOP
 *
 * @author Werner Punz
 */
public class ScriptingApplicationFactory extends ApplicationFactory implements Decorated
{

    ApplicationFactory _delegate;


    public ScriptingApplicationFactory(ApplicationFactory delegate) {
        _delegate = delegate;

    }

    public Application getApplication() {
        Application retVal = _delegate.getApplication();

        if (WeavingContext.getInstance().isScriptingEnabled()  && !(retVal instanceof ApplicationProxy))
            retVal = new ApplicationProxy(retVal);

        return retVal;
    }

    public void setApplication(Application application) {
        if (WeavingContext.getInstance().isScriptingEnabled() && !(application instanceof ApplicationProxy))
            application = new ApplicationProxy(application);

        _delegate.setApplication(application);
    }

    @Override
    public ApplicationFactory getWrapped() {
        return _delegate.getWrapped();
    }

    public Object getDelegate() {
        return _delegate;
    }
}
