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
package org.apache.myfaces.scripting.core.scanEvents.events;

import org.apache.myfaces.scripting.core.scanEvents.SystemEvent;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public abstract class BaseEvent implements SystemEvent {

    public static final int ARTEFACT_TYPE_UNKNOWN = -1;
    public static final int ARTEFACT_TYPE_MANAGEDBEAN = 1;
    public static final int ARTEFACT_TYPE_MANAGEDPROPERTY = 2;
    public static final int ARTEFACT_TYPE_RENDERKIT = 3;
    public static final int ARTEFACT_TYPE_VIEWHANDLER = 4;
    public static final int ARTEFACT_TYPE_RENDERER = 5;
    public static final int ARTEFACT_TYPE_COMPONENT = 6;
    public static final int ARTEFACT_TYPE_VALIDATOR = 7;
    public static final int ARTEFACT_TYPE_BEHAVIOR = 8;


    private String _scannedClass = null;
    private int _artefactType = ARTEFACT_TYPE_UNKNOWN;

    protected BaseEvent(String scannedClass) {
        _scannedClass = scannedClass;
    }

    protected BaseEvent(String scannedClass, int artefactType) {
        _scannedClass = scannedClass;
        _artefactType = artefactType;
    }

    public String getAffectedClassName() {
        return _scannedClass;  
    }

    public Integer getEventType() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Integer getArtefactType() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
