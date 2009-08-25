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
/**
 * @author Werner Punz
 */
class TestBean {
    String helloworld = "hallo ist - die bean"

    //note you can add new atrtributes
    //no setter or getter is needed
    //and with a single request you will get the
    //attribute be used in your page
    //give it a try

    //String newAttribute = "This is a new attribute"
    //uncomment this and add a control to the page displaying it


    public String getHelloworld() {
        return helloworld
    }


    public String doit() {
        print "doit S "
        return null
    }


    public String getXxx() {
        "xxx Simple text you can change me on the fly"
    }
}