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

package org.apache.myfaces.extensions.scripting.jsf.components;

import javax.el.ValueExpression;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;

/**
 * Component which allows to check which files
 * have been marked as possibly modified in the recent history
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class TaintHistory extends UIOutput {

    public static final int DEFAULT_NO_ENTRIES = 10;

    Integer _noEntries;
    String _filter;
    private static final String RENDERER_TYPE = "org.apache.myfaces.extensions.scripting.components.TaintHistoryRenderer";
    private static final String NO_ENTRIES = "noEntries";

    public TaintHistory() {
        setRendererType(RENDERER_TYPE);
    }

    @SuppressWarnings("unused")
    public void setNoEntries(Integer entries) {
        _noEntries = entries;
    }

    @Override
    public Object saveState(FacesContext facesContext) {
        Object values[] = new Object[3];
        values[0] = super.saveState(facesContext);    //To change body of overridden methods use File | Settings | File Templates.
        values[1] = _noEntries;
        values[2] = _filter;
        return values;
    }

    @Override
    public void restoreState(FacesContext facesContext, Object state) {
        Object[] values = (Object[]) state;
        super.restoreState(facesContext, values[0]);
        _noEntries = (Integer) values[1];
        _filter = (String) values[2];
    }

    public Integer getNoEntries() {
        if (_noEntries != null) {
            return _noEntries;
        }
        ValueExpression vb = getValueExpression(NO_ENTRIES);
        return vb != null ? ((Integer) vb.getValue(getFacesContext().getELContext())) : DEFAULT_NO_ENTRIES;
    }

    @SuppressWarnings("unused")
    public void setFilter(String filter) {
        _filter = filter;
    }

    @SuppressWarnings("unused")
    public String getFilter() {
        if (_filter != null) {
            return _filter;
        }
        ValueExpression vb = getValueExpression(NO_ENTRIES);
        return vb != null ? ((String) vb.getValue(getFacesContext().getELContext())) : null;
    }
}
