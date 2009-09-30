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
package org.apache.myfaces.scripting.core.scanEvents;

import static org.junit.Assert.*;
import org.junit.Test;
import org.apache.myfaces.scripting.core.scanEvents.events.AnnotatedArtefactRemovedEvent;
import org.apache.myfaces.scripting.core.scanEvents.events.ClassRefreshedEvent;

import java.util.Set;
import java.util.HashSet;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class EventSystemTest {

    boolean succeeded = false;
    SystemEventProcessor processor = new SystemEventProcessor();


    SystemEventListener listener1 = new SystemEventListener() {
        public Set<Integer> supportsEvents() {
            Set<Integer> supports = new HashSet<Integer>();
            supports.add(ClassRefreshedEvent.EVT_TYPE_CLASSREFRESH);
            return supports;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public void handleEvent(SystemEvent evt) {
            succeeded = true;
        }
    };

    SystemEventListener listener2 = new SystemEventListener() {
        public Set<Integer> supportsEvents() {
            Set<Integer> supports = new HashSet<Integer>();
            supports.add(AnnotatedArtefactRemovedEvent.EVT_TYPE_BEAN_REMOVED);
            return supports;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public void handleEvent(SystemEvent evt) {
            fail("should not be called");
        }
    };


   

    @Test
    public void testEventHandling() {
        processor.addListener(listener1);
        processor.addListener(listener2);

        processor.dispatchEvent(new ClassRefreshedEvent("java.lang.Object"));

        assertTrue("Event dispatching succeeded", succeeded);
    }

}
