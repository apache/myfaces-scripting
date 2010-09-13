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
package org.apache.myfaces.extensions.scripting.servlet;

import org.apache.myfaces.extensions.scripting.api.ScriptingWeaver;
import org.apache.myfaces.extensions.scripting.core.util.WeavingContext;
import org.apache.myfaces.shared_impl.util.ClassLoaderExtension;

import javax.servlet.ServletContext;
import java.util.logging.Logger;

/**
 * a custom chainloader which adds a groovy loading
 * facility to our myfaces loading plugin system
 *
 * @author Werner Punz
 */
@SuppressWarnings("unused")
public class CustomChainLoader extends ClassLoaderExtension {

    /*
       * servlet context init var for additional chain loader paths which have
       * higher priority than the default ones 
       */

    ScriptingWeaver _scriptingWeaver = null;

    public CustomChainLoader(ServletContext servletContext) {
        _scriptingWeaver = WeavingContext.getWeaver();
    }


    public Class forName(String name) {
        if(_scriptingWeaver == null || name == null) {
            return null;
        }
        
        if (name.endsWith(";")) {
            name = name.substring(1, name.length() - 1);
        }

        if (name.startsWith("java.")) /*the entire java namespace is reserved so no use to do a specific classloading check here*/
            return null;
        if (name.startsWith("javax.")) /*the entire java namespace is reserved so no use to do a specific classloading check here*/
            return null;
        else if (name.startsWith("com.sun")) /*internal java specific namespace*/
            return null;
        else if (name.startsWith("sun.")) /*internal java specific namespace*/
            return null;
        else if (name.startsWith("org.apache") && !name.startsWith("org.apache.myfaces")) {
            return null;
        }
        if(name.contains("JavaTestComponent")){
            System.out.println("Debugpoint found");    
        }
        return _scriptingWeaver.loadScriptingClassFromName(name);
    }

   

}