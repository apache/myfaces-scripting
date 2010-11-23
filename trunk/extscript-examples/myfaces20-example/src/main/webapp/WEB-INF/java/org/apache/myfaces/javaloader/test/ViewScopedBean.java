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

package org.apache.myfaces.javaloader.test;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Testing case for a view scoped bean 
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

@ManagedBean
@ViewScoped
public class ViewScopedBean implements Serializable {

    int cnt = 0;
    String helloWorld = "hello world from my view scoped bean";
    transient Logger _logger = Logger.getLogger(ViewScopedBean.class.getName());

    public ViewScopedBean() {
        cnt = 0;
        _logger.info("Init bean");
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public String getHelloWorld() {
        return helloWorld + (cnt++);
    }

    public void setHelloWorld(String helloWorld) {
        this.helloWorld = helloWorld;
    }

    private void readObject(ObjectInputStream ois) throws Exception {
        _logger = Logger.getLogger(ViewScopedBean.class.getName());
        _logger.info("Restoring bean which was savestated");
        ois.defaultReadObject();
    }

    public String doAction() {
        return null;
    }

}
