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
package org.apache.myfaces.groovyloader.core;

import org.apache.myfaces.groovyloader.core.GroovyWeaver;

import javax.servlet.ServletContext;
import java.net.URL;
import java.net.URLClassLoader;
import java.io.File;


/**
 * Groovy Classloader
 * which does some delegating class loading on existing
 * names
 * it looks up for groovy files and if it finds them and no loads them in favor
 * of existing classes
 *
 *
 * TODO We can eliminate this class
 * as soon as we have our extension points in place
 *
 * @author Werner Punz
 */
public class DelegatingGroovyClassloader extends URLClassLoader {

    String _classRoot = "";
    String _groovyRoot = "";
    String _testRoot = "/Users/werpu/Desktop/myfaces-groovy/core/src/main/groovy/";
    GroovyWeaver _groovyWeaver = null;

    public GroovyWeaver getGroovyFactory() {
        return _groovyWeaver;
    }

    public void setGroovyFactory(GroovyWeaver groovyWeaver) {
        this._groovyWeaver = groovyWeaver;
    }


    public String getClassRoot() {
        return _classRoot;
    }

    public void setClassRoot(String classRoot) {
        this._classRoot = classRoot;
    }

    public String getGroovyRoot() {
        return _groovyRoot;
    }

    public void setGroovyRoot(String groovyRoot) {
        this._groovyRoot = groovyRoot;
    }

    public String getTestRoot() {
        return _testRoot;
    }

    public void setTestRoot(String testRoot) {
        this._testRoot = testRoot;
    }


    /**
     * helper to determine the real context classloader
     * some app servers switch the context classloaders relatively late
     * while all being active, affected app servers, oc4j, was probably
     * ever ear container, usually if no context classloader can be
     * found a fallback to the classloader of the
     * current class is safe, the jar however has to reside within the webapp
     * (sorry no root jar is possible that way)
     *
     * @return
     */
    public static ClassLoader getRealContextClassloader() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String resource = DelegatingGroovyClassloader.class.getCanonicalName().replaceAll("\\.", "/") + ".class";
        URL in = loader.getResource(resource);
        if (in == null) { //we are in a different loader, some ear containers are at the ear level at this stage if it is a webapp context
            loader = DelegatingGroovyClassloader.class.getClassLoader();
            in = loader.getResource(resource);
            if (in == null) {
                return null;
            }
            return loader;

        }
        return loader;
    }


    public DelegatingGroovyClassloader(ServletContext servletContext) {
        super(new URL[0], getRealContextClassloader());

        this._groovyWeaver = new GroovyWeaver();;
        String contextRoot = servletContext.getRealPath("/WEB-INF/groovy/");

        contextRoot = contextRoot.trim();
        if(!contextRoot.endsWith("/") && !contextRoot.endsWith("\\"))
            contextRoot += "/";
        _groovyRoot = contextRoot;

    }

    /**
     * the loadclass is needed for the bean and artefact loading
     * every class loaded must be processed at least once if present
     * as groovy file so that we can mark it and have it checked for being
     * tainted, also we need an initial load mechanism for uncompiled groovy files
     * in our WEB-INF/classes dir so that we can load them properly
     *
     * TODO this applies mostly for beans we should check if we cannot
     * simply do the loading from the el resolvers point of view in our case
     * then we probably would have eliminated teh classloader entirely
     *
     * @param s
     * @return
     * @throws ClassNotFoundException
     */
    public Class loadClass(String s) throws ClassNotFoundException {
       
        if (s.startsWith("java.")) /*the entire java namespace is reserved so no use to do a specific classloading check here*/
            return super.loadClass(s);
        else if (s.startsWith("com.sun")) /*internal java specific namespace*/
            return super.loadClass(s);

        String groovyClass = s.replaceAll("\\.", "/") + ".groovy";
        File classFile = new File(_groovyRoot + groovyClass);

        if (classFile.exists()) /*we check the groovy subdir for our class*/
            return (Class) _groovyWeaver.loadScriptingClassFromFile(_groovyRoot + groovyClass);

        classFile = new File(_classRoot + groovyClass);
        if (classFile.exists()) /*now lets check our class subdir for a groovy file to be loaded*/
            return (Class) _groovyWeaver.loadScriptingClassFromFile(_classRoot + groovyClass);

        /*standard testcase*/
        classFile = new File(_testRoot + groovyClass);


        if (classFile.exists()) /*now lets check our class subdir for a groovy file to be loaded*/
            return _groovyWeaver.loadScriptingClassFromFile(_testRoot + groovyClass);

        /**
         * if no groovy source is found we delegate to our parent classloader for loading
         * our classes on the fly
         * */
        return super.loadClass(s);

    }


}
