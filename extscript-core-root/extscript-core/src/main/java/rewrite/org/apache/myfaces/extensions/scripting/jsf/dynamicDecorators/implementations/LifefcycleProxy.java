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
package rewrite.org.apache.myfaces.extensions.scripting.jsf.dynamicdecorators.implementations;


import rewrite.org.apache.myfaces.extensions.scripting.core.api.Decorated;
import rewrite.org.apache.myfaces.extensions.scripting.core.api.ScriptingConst;
import rewrite.org.apache.myfaces.extensions.scripting.core.context.WeavingContext;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;

/**
 * Scripting enabled lifecycle
 *
 * @author Werner Punz
 */
public class LifefcycleProxy extends Lifecycle implements Decorated
{

    Lifecycle _delegate = null;

    private void weaveDelegate() {
        if (_delegate != null)
            _delegate = (Lifecycle) WeavingContext.getInstance().reload(_delegate,
                    ScriptingConst.ARTIFACT_TYPE_LIFECYCLE);
    }

    public LifefcycleProxy(Lifecycle delegate) {
        _delegate = delegate;
    }

    public void addPhaseListener(PhaseListener phaseListener) {
        weaveDelegate();
        /*we can put our object weaving code into the add here*/
        if (WeavingContext.getInstance().isDynamic(phaseListener.getClass()))
            phaseListener = (PhaseListener) WeavingContext.createMethodReloadingProxyFromObject(phaseListener, PhaseListener.class, ScriptingConst.ARTIFACT_TYPE_PHASELISTENER);

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

    public Object getDelegate() {
        return _delegate;
    }
}
