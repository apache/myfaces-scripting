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

package org.apache.myfaces.javaloader.elResolverTest;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *
 * Testcase for intra dependency detection, now if you use the bean generated
 * from the bean factory the Receiver bean will not be refreshed
 * because we do not have any intra class dependencies
 * This dependency mismatch will be lifted in the long run but for now
 * we have to live with it
 *
 */

@ManagedBean (name="receiver")
@SessionScoped
public class Receiver {

    @ManagedProperty(value = "#{myFactory['booga']}")
    Object myBean;

    public Object getMyBean() {
        return myBean;
    }         

    public void setMyBean(Object myBean) {
        this.myBean = (MyBean ) myBean;
    }
}
