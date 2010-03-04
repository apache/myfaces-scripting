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

package org.apache.myfaces.javaloader.componentTest;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIOutput;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
@FacesComponent("org.apache.myfaces.scripting.components.TaintHistory")
public class TaintHistory extends UIOutput {

    static final int DEFAULT_NO_ENTRIES = 10;

    enum PropertyKeys {
        noEntries, filter
    }

    public TaintHistory() {
        setRendererType("org.apache.myfaces.scripting.components.TaintHistoryRenderer");
    }

    public void setNoEntries(int entries) {
        if (entries < 0)
            entries = DEFAULT_NO_ENTRIES;
        getStateHelper().put(PropertyKeys.noEntries, entries);
    }

    public int getNoEntries() {
        Integer retVal = (Integer) getStateHelper().eval(PropertyKeys.noEntries);
        if (retVal == null) {
            retVal = DEFAULT_NO_ENTRIES;
        }
        return retVal;
    }

    public void setFilter(int filter) {
        getStateHelper().put(PropertyKeys.filter, filter);
    }

    public String getFilter() {
        return (String) getStateHelper().eval(PropertyKeys.filter);
    }
}
