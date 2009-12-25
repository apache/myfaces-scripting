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
package org.apache.myfaces.scripting.startup;

import org.apache.myfaces.scripting.core.util.WeavingContext;
import org.apache.myfaces.scripting.api.ScriptingWeaver;

import javax.faces.event.SystemEventListener;
import javax.faces.event.SystemEvent;
import javax.faces.event.PostConstructApplicationEvent;
import javax.faces.application.Application;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          we do the initial source scan after the entire application has started up
 *          we now can reuse our jsf2 system event faclities by placing
 *          a listener to the application startup
 */

public class IntialScanAnnotationListener implements SystemEventListener {
    public boolean isListenerForSource(Object source) {
        return source instanceof Application;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void processEvent(SystemEvent event) {
        if (!event.getClass().equals(PostConstructApplicationEvent.class)) {
            return;
        }
        //we can rely on being in the same thread as the original
        //startup context listener, so the initial weaver still is activated
        ScriptingWeaver weaver = WeavingContext.getWeaver();

        weaver.fullRecompile();
        //we now do a full source or precompiled annotation scan
        //the entire scripting subsystem should be initialized by now
        weaver.fullClassScan();
    }
}
