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
package org.apache.myfaces.scripting.jsf.dynamicdecorators.implemetations;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.scripting.api.Decorated;
import org.apache.myfaces.scripting.api.ScriptingConst;
import org.apache.myfaces.scripting.core.util.WeavingContext;

import javax.el.*;
import java.beans.FeatureDescriptor;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * EL Resolver which is scripting enabled
 *
 * @author Werner Punz
 */
public class ELResolverProxy extends ELResolver implements Decorated {
    Log log = LogFactory.getLog(ELResolverProxy.class);



    public Object getValue(ELContext elContext, final Object base, final Object property) throws NullPointerException, PropertyNotFoundException, ELException {
        //request, class is loaded anew hence we already have picked up the new code

        Object retVal = _delegate.getValue(elContext, base, property);

        if (retVal != null && WeavingContext.isDynamic(retVal.getClass())) {
            //now here we have something special which is implicit
            //if the bean is only request scoped we dont have to reload anything
            //so just run through this code without having anything happening here
            //reloadScriptingInstance will return the same object we already had before
            //the reason is for request or none scoped beans we get a new
            //freshly reloaded and compiled instance on every request
            //the problem starts with session application or custom scoped beans
            //There nothing is compiled and we have to do the further bean processing

            //TODO move the bean dropping into the beginning of the lifecycle instead of on demand
            //That way we have a cleaner control over the refresh per request
            


            Object newRetVal = WeavingContext.getWeaver().reloadScriptingInstance(retVal, ScriptingConst.ARTEFACT_TYPE_MANAGEDBEAN); /*once it was tainted or loaded by
                 our classloader we have to recreate all the time to avoid classloader issues*/
            if (newRetVal != retVal) {
                _delegate.setValue(elContext, base, property, newRetVal);
            }
            return newRetVal;
        }

        return retVal;

    }

    public Class<?> getType(ELContext elContext, Object o, Object o1) throws NullPointerException, PropertyNotFoundException, ELException {
        Class<?> retVal = _delegate.getType(elContext, o, o1);
        if (retVal != null && WeavingContext.isDynamic((Class) retVal)) {
            return WeavingContext.getWeaver().reloadScriptingClass((Class) retVal);
        }
        return retVal;
    }

    public void setValue(ELContext elContext, Object o, Object o1, Object o2) throws NullPointerException, PropertyNotFoundException, PropertyNotWritableException, ELException {
        _delegate.setValue(elContext, o, o1, o2);
    }

    public boolean isReadOnly(ELContext elContext, Object o, Object o1) throws NullPointerException, PropertyNotFoundException, ELException {
        return _delegate.isReadOnly(elContext, o, o1);
    }

    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext elContext, Object o) {
        return _delegate.getFeatureDescriptors(elContext, o);
    }

    public Class<?> getCommonPropertyType(ELContext elContext, Object o) {
        return _delegate.getCommonPropertyType(elContext, o);
    }

    public ELResolverProxy(ELResolver delegate) {
        _delegate = delegate;
    }

    ELResolver _delegate = null;

    public Object getDelegate() {
        return _delegate;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Set<Integer> supportsEvents() {
        Set<Integer> supports = new HashSet<Integer>();
        supports.add(ScriptingConst.ARTEFACT_TYPE_MANAGEDBEAN);

        return supports;  //To change body of implemented methods use File | Settings | File Templates.
    }

    
}
