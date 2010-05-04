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

package org.apache.myfaces.javaloader.instantbean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

@ManagedBean
@SessionScoped
public class HelloBean {

    int cnt = 0;
    //we deliberately make a warning here to cover warnings in our compiler interface
    List testList = new LinkedList();

    private String hello = "Hello world from an instant bean";

    public HelloBean() {
        //unchecked operation deliberately done
        testList.add(hello);
    }

    private String addedMethod() {
        return "you can add change and remove methods on the fly without any server restart";
    }

    public String getHello() {
        return hello ;
    }


    public String getHello2() {
        return hello ;
    }
    public void setHello(String hello) {
        this.hello = hello;
    }
    public void setHello2(String hello) {
        this.hello = hello;
    }

    public int getCnt() {
        return ++cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }
}
