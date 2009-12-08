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
package org.apache.myfaces.groovyloader.core

import org.apache.myfaces.groovyloader.core.GroovyWeaver
import org.apache.myfaces.scripting.loaders.groovy.MetaclassStubcompilerFix
import org.apache.myfaces.scripting.core.util.WeavingContext
import org.apache.myfaces.scripting.core.util.WeavingContext

/**
 * A proxying class doing constructor interceoption
 * to enable dynamic reload of groovy classes
 * on constructor setup
 *
 * Every groovy class
 * generated is marked with this
 * procy on its metaclass
 * however java omits the meta data
 * this proxy is only used for groovy 2 groovy
 * references and should allow code reloading
 * on an instantiation level wherever possible
 *
 * This is a first attempt at
 * reloading within groovy
 * we might expand this later on
 * for on the fly method replacement as well
 *
 * @author Werner Punz
 *
 */
class Groovy2GroovyObjectReloadingProxy extends MetaclassStubcompilerFix {
/*
due to a bug in the groovy meta stub compiler
we have to derive from our own object here
*/

    boolean tainted = false

    GroovyWeaver weaver = null

    Groovy2GroovyObjectReloadingProxy(Class aClass) {
        super(aClass)
        initialize()
    }

    public Groovy2GroovyObjectReloadingProxy(MetaClass metaClass) {
        super(metaClass);    //To change body of overridden methods use File | Settings | File Templates.
        // initialize()
    }


    protected Object addMethodProxy(Object a_object) {
        //TODO isolate this so that we have a generic solution
        //and a jsf solution

    }


    /**
     * originally we tried invoke on method
     * metaclass replacement
     * that did not work out
     * a new class is generated
     * on every instance
     * so we intercept simply the object
     * creation and do the reloading there
     */
    public Object invokeConstructor(Object[] objects) {
        Object a_object = super.invokeConstructor(objects);    //To change body of overridden methods use File | Settings | File Templates.

        if (weaver == null)
            weaver = WeavingContext.getWeaver();

        a_object = weaver.reloadScriptingInstance(a_object);

        return a_object;

    }

}