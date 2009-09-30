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

import org.apache.myfaces.scripting.core.scanEvents.events.BaseEvent;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class ObjectRefreshedEvent extends BaseEvent {

    Object _origin;
    Object _target;
    private static final int EVT_TYPE_OBJECT_REFRESHED = 3;

    public ObjectRefreshedEvent(Object origin, Object target) {
        super(origin.getClass().getName());
        _origin = origin;
        _target = target;
    }

    public ObjectRefreshedEvent( int artefactType, Object origin, Object target) {
        super(origin.getClass().getName(), artefactType);
        _origin = origin;
        _target = target;
    }

    public Integer getEventType() {
        return EVT_TYPE_OBJECT_REFRESHED;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
