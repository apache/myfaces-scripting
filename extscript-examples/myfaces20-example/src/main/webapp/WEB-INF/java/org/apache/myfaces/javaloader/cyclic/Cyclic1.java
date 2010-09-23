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
 *
 * This is merely a testcase for the system internal dependency scanner
 * to prove that cyclic class dependencies can be resolved properly
 * you can safely ignore this class
 */
public class Cyclic1 {
    Cyclic2 cycl2 = null;

    public Cyclic2 getCycl2() {
        return cycl2;
    }

    public void setCycl2(Cyclic2 cycl2) {
        this.cycl2 = cycl2;
    }
}
