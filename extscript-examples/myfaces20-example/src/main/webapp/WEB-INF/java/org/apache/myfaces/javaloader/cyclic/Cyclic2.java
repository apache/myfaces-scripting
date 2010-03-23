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
package org.apache.myfaces.javaloader.cyclic;

/**
 * cyclic dependency class test for our class scanner
 */
public class Cyclic2 {

    static Cyclic2 instance = null;

    Cyclic1 cycl1 = null;

    public Cyclic1 getCycl1() {
        return cycl1;
    }

    public void setCycl1(Cyclic1 cycl1) {
        this.cycl1 = cycl1;
    }

    public static Cyclic2 getInstance() {
        return instance;
    }

    public static void setInstance(Cyclic2 instance) {
        Cyclic2.instance = instance;
    }
}
