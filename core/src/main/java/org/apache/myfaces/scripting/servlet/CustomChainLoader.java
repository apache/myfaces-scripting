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
package org.apache.myfaces.scripting.servlet;

import org.apache.myfaces.groovyloader.core.GroovyWeaver;
import org.apache.myfaces.scripting.api.ScriptingWeaver;
import org.apache.myfaces.scripting.core.util.ProxyUtils;
import org.apache.myfaces.shared_impl.util.ClassLoaderExtension;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletContext;

/**
 * a custom chainloader which adds a groovy loading
 * facility to our myfaces loading plugin system
 *
 * @author Werner Punz
 */
public class CustomChainLoader extends ClassLoaderExtension {

    /*
       * servlet context init var for additional chain loader paths which have
       * higher priority than the default ones 
       */
    static String CUSTOM_LOADER_PATHS = "org.apache.myfaces.scripting.groovy.LOADER_PATHS";


    String classRoot = "";
    String scriptingRoot = "";
    ScriptingWeaver scriptingWeaver = null;

    public CustomChainLoader(ServletContext servletContext) {
        this.scriptingWeaver = new GroovyWeaver();

        String contextRoot = servletContext.getRealPath("/WEB-INF/groovy/");

        contextRoot = contextRoot.trim();
        if (!contextRoot.endsWith("/") && !contextRoot.endsWith("\\"))
            contextRoot += "/";
        scriptingRoot = contextRoot;

        String additionalGroovyLoaderPaths = servletContext.getInitParameter(CUSTOM_LOADER_PATHS);
        if(!StringUtils.isBlank(additionalGroovyLoaderPaths)) {
            String [] additionalPaths = additionalGroovyLoaderPaths.split(",");
            for(String path: additionalPaths) {
                 this.scriptingWeaver.appendCustomScriptPath(path);    
            }
        }

        this.scriptingWeaver.appendCustomScriptPath(scriptingRoot);
        this.scriptingWeaver.appendCustomScriptPath(classRoot);
        //we have to store it because our filter
        //does not trigger upon initialisation
        ProxyUtils.setWeaver(this.scriptingWeaver);
    }

    public Class forName(String name) {

        if (name.startsWith("java.")) /*the entire java namespace is reserved so no use to do a specific classloading check here*/
            return null;
        if (name.startsWith("javax.")) /*the entire java namespace is reserved so no use to do a specific classloading check here*/
            return null;
        else if (name.startsWith("com.sun")) /*internal java specific namespace*/
            return null;

        return scriptingWeaver.loadScriptingClassFromName(name);
    }

    public ScriptingWeaver getScriptingWeaver() {
        return scriptingWeaver;
    }

    public void setScriptingWeaver(ScriptingWeaver scriptingWeaver) {
        this.scriptingWeaver = scriptingWeaver;
    }

}