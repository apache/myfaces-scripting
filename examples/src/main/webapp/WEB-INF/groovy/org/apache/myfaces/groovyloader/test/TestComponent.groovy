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
package org.apache.myfaces.groovyloader.test

import javax.faces.component.UIInput
import javax.faces.context.FacesContext
import javax.faces.el.ValueBinding

/**
 * @author Werner Punz
 */
public class TestComponent extends UIInput {

    private static final String DEFAULT_RENDERER_TYPE2 = "org.apache.myfaces.groovyloader.test.Test";

    String _testattr = "component text";
    def _testattr_changed = true;
    def testattr_changed = true;


    public TestComponent() {
        super()
        setRendererType(DEFAULT_RENDERER_TYPE2)
    }

    public Object saveState(FacesContext context) {
        def values = []
        values[0] = super.saveState(context)
        values[1] = testattr
        return values.toArray()
    }

    public void restoreState(FacesContext context, Object state) {
        super.restoreState(context, state[0]);
        _testattr = state[1]
    }

    public void setTestattr(String attr) {
        _testattr = attr
    }

    public String getTestattr() {
        if (_testattr != null)
            return _testattr

        ValueBinding vb = getValueBinding("testattr")
        String v = vb != null ? (String) vb.getValue(getFacesContext()) : null
        return v != null ? v : ""

    }


    public String getFamily() {
        return "javax.faces.Input";
    }

}
