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

import org.apache.myfaces.javaloader.other.Markable;

import javax.faces.component.UIInput;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIOutput;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *
 * A simple test component which utilizes the jsf2 annotation API
 * for and can be reprogrammed on the fly, after a refresh the changes
 * within the component can be picked up
 *
 */

@FacesComponent("at.irian.JavaTestComponent")
public class JavaTestComponent extends UIInput implements Markable {

    String _testAttr;

    //enum PropertyKeys {
    //    inc, testAttr, testAttr2, testAttr3, testAttr4
    //}

    public JavaTestComponent() {
        setRendererType("at.irian.JavaTestRenderer");
    }

    public String getMarker() {
        return "<h4>Component 1 marker</h4>";
    }

    public void setMarker() {

    }

    public int getInc() {
        return (Integer) getStateHelper().eval("inc", 1);
    }

    public void setInc(int inc) {
        getStateHelper().put("inc", inc);
    }

    public String getTestAttr() {
        return (String) getStateHelper().eval("testAttr", "");
    }

    public void setTestAttr(String testAttr) {
        getStateHelper().put("testAttr", testAttr);
    }

    public String getTestAttr2x() {
        return (String) getStateHelper().eval("testAttr2x", "");
    }

    public void setTestAttr2x(String testAttr) {
        getStateHelper().put("testAttr2x", testAttr);
    }

    public String getTestAttr3() {
        return (String) getStateHelper().eval("testAttr3x", "");
    }

    public void setTestAttr3(String testAttr) {
        getStateHelper().put("testAttr3x", testAttr);
    }

    /**
     * Ok guys, lets add a new attribute to the component
     * which is displayed in our browser page
     * <p/>
     * Note we are on jsf 2 level
     */

    public String getTestAttr4() {
        return (String) getStateHelper().eval("testAttr4x", "");
    }

    public void setTestAttr4(String testAttr) {
        getStateHelper().put("testAttr4x", testAttr);
    }

    

    public String getMyHello() {
        return "myHelloworld";
    }

}
