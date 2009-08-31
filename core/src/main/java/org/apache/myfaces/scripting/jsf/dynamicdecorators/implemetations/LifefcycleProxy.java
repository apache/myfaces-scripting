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
package org.apache.myfaces.scripting.jsf.dynamicdecorators.implemetations;

import org.apache.myfaces.scripting.api.Decorated;
import org.apache.myfaces.scripting.core.util.ProxyUtils;

import javax.faces.lifecycle.Lifecycle;
import javax.faces.event.PhaseListener;
import javax.faces.context.FacesContext;
import javax.faces.FacesException;

/**
 * Scripting enabled lifecycle
 *
 * @author Werner Punz
 */
public class LifefcycleProxy extends Lifecycle implements Decorated {

 //   GroovyWeaver ProxyUtils.getWeaver() =  ProxyUtils.getWeaver();

    private void weaveDelegate() {
        if(_delegate != null)
            _delegate = (Lifecycle) ProxyUtils.getWeaver().reloadScriptingInstance(_delegate);
    }


    public LifefcycleProxy(Lifecycle delegate) {
        _delegate = delegate;
    }

    public void addPhaseListener(PhaseListener phaseListener) {
        weaveDelegate();
        /*we can put our object weaving code into the add here*/
        if (ProxyUtils.isDynamic(phaseListener.getClass()))
            phaseListener = (PhaseListener)  ProxyUtils.createMethodReloadingProxyFromObject(phaseListener, PhaseListener.class);

        _delegate.addPhaseListener(phaseListener);
    }

    public void execute(FacesContext facesContext) throws FacesException {
        weaveDelegate();
        _delegate.execute(facesContext);
    }

    public PhaseListener[] getPhaseListeners() {
        weaveDelegate();
        return _delegate.getPhaseListeners();
    }

    public void removePhaseListener(PhaseListener phaseListener) {
        weaveDelegate();
        _delegate.removePhaseListener(phaseListener);
    }

    public void render(FacesContext facesContext) throws FacesException {
        weaveDelegate();
        _delegate.render(facesContext);
    }

    Lifecycle _delegate = null;

    public Object getDelegate() {
        return _delegate;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
