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

import org.apache.myfaces.scripting.jsf.dynamicdecorators.implemetations.FacesContextProxy;
import org.apache.myfaces.scripting.api.Decorated;
import org.apache.myfaces.scripting.core.util.ProxyUtils;

import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.FacesException;

/**
 * Faces context weaver which builds
 * our reloading proxy around the current faces context
 *
 * @author Werner Punz
 */
public class ScriptingFacesContextFactory extends javax.faces.context.FacesContextFactory implements Decorated {

    public FacesContextFactory _delegate;
    boolean scriptingEnabled = false;

    public ScriptingFacesContextFactory(FacesContextFactory delegate) {
        _delegate = delegate;
        scriptingEnabled = ProxyUtils.isScriptingEnabled();
    }

    public void setDelegate(FacesContextFactory delegate) {
        _delegate = delegate;
    }

    public FacesContext getFacesContext(Object o, Object o1, Object o2, Lifecycle lifecycle) throws FacesException {
        FacesContext retVal = _delegate.getFacesContext(o, o1, o2, lifecycle);  //To change body of implemented methods use File | Settings | File Templates.
        //TODO check if we weave thise around our original
        //faces context to bypass our groovy dynamic reflection problems
        //TODO this is not fully done yet, the faces context is not
        //Woven around our method reloading weaver or our instantiation
        //mechanism
        if (scriptingEnabled && !(retVal instanceof FacesContextProxy))
            return new FacesContextProxy(retVal);
        return retVal;
    }

    public Object getDelegate() {
        return _delegate;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
