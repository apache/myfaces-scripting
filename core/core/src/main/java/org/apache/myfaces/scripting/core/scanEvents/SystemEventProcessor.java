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

import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          Scan events, which operate on a request scale
 *          reachable via the proxy utils
 *          <p/>
 *          We have to add an event processing system here
 *          because submodules which are dynamically bound
 *          sometimes have to notify the core about the loading
 *          and refreshing state
 */

public class SystemEventProcessor {
    Set<SystemEventListener> _listeners = Collections.synchronizedSet(new HashSet<SystemEventListener>());

    public void addListener(SystemEventListener listener) {
        _listeners.add(listener);
    }

    public boolean hasListener(SystemEventListener listener) {
        return _listeners.contains(listener);
    }

    public void removeListener(SystemEventListener listener) {
        _listeners.remove(listener);
    }

    public void clear() {
        _listeners.clear();
    }

    public void dispatchEvent(SystemEvent event) {
        //we only trigger on events which have a registered listener
        for (SystemEventListener listener : _listeners) {
            if (listener.supportsEvents().contains(event.getEventType())) {
                listener.handleEvent(event);
            }
        }
    }
}
