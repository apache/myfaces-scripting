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

package org.apache.myfaces.scripting.jsf;

import org.apache.myfaces.scripting.core.support.MockServletContext;
import org.apache.myfaces.scripting.core.util.WeavingContextInitializer;
import org.apache.myfaces.test.base.AbstractJsfTestCase;
import org.apache.myfaces.test.mock.lifecycle.MockLifecycle;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class RefreshPhaseListenerTest extends AbstractJsfTestCase {
    MockServletContext context;

    boolean executed = false;
    Runnable runner;

    RefreshPhaseListener probe;


    public RefreshPhaseListenerTest() {
        super(RefreshPhaseListenerTest.class.getName());
    }

    public void setUp() throws Exception {
        super.setUp();
        probe = new RefreshPhaseListener();
        context = new MockServletContext();
        WeavingContextInitializer.initWeavingContext(context);

        runner = new Runnable() {
            public void run() {
                executed = true;
            }
        };
    }

    public void testCalling1() throws Exception {
        RefreshPhaseListener.applyAction(runner);

        assertTrue(probe.getPhaseId() == PhaseId.ANY_PHASE);
        probe.beforePhase(new PhaseEvent(facesContext, PhaseId.RESTORE_VIEW, new MockLifecycle()));
        assertTrue(executed);
        executed = false;
        probe.beforePhase(new PhaseEvent(facesContext, PhaseId.APPLY_REQUEST_VALUES,  new MockLifecycle()));
        assertFalse(executed);

    }

    public void testAfterPhase() {
        probe.afterPhase(new PhaseEvent(facesContext, PhaseId.RESTORE_VIEW, new MockLifecycle()));
    }

}
