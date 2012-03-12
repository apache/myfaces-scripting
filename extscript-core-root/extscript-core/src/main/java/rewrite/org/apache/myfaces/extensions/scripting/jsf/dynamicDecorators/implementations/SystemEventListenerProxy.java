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

import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

/**
 * a method level reloading proxy class
 * we do not use auto proxies here because
 * this class needs special treatment
 * over our decorated interface
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class SystemEventListenerProxy implements Decorated, SystemEventListener {

    SystemEventListener _delegate;

    public SystemEventListenerProxy(SystemEventListener delegate) {
        _delegate = delegate;
    }

    public boolean isListenerForSource(Object source) {
        weaveDelegate();
        return _delegate.isListenerForSource(source);
    }

    public void processEvent(SystemEvent event) {
        weaveDelegate();
        _delegate.processEvent(event);
    }

    @Override
    public Object getDelegate() {
        return _delegate;
    }

    private void weaveDelegate() {
        //TODO (1.1) add a speed optimization here by pushing something in the request map
        if (_delegate != null) {
            _delegate = (SystemEventListener) WeavingContext.getInstance().reload(_delegate,
                    ScriptingConst.ARTIFACT_TYPE_SYSTEMEVENTLISTENER);
        }
    }
}
