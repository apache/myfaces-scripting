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
package org.apache.myfaces.extensions.scripting.jsf.dynamicdecorators.implemetations;

import org.apache.myfaces.extensions.scripting.api.Decorated;
import org.apache.myfaces.extensions.scripting.api.ScriptingConst;
import org.apache.myfaces.extensions.scripting.core.util.WeavingContext;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * EL Resolver which is scripting enabled
 *
 * @author Werner Punz
 *
 * TODO not needed anymore because we drop the beans at request start directly which are tainted...
 * they get reloaded because they are dropped entirely from the scope
 * 
 * The compile and load happens on classloader level
 * @deprecated
 */
public class ELResolverProxy extends ELResolver implements Decorated {

    Logger log = Logger.getLogger(ELResolverProxy.class.getName());
    ELResolver _delegate = null;

   // static ThreadLocal<Boolean> _getValue = new ThreadLocal<Boolean>();

    public Object getValue(ELContext elContext, final Object base, final Object property) throws NullPointerException, ELException {

        Object retVal = _delegate.getValue(elContext, base, property);

      /*  Object newRetVal;
        if (retVal != null && WeavingContext.isDynamic(retVal.getClass())) {

            newRetVal = WeavingContext.getWeaver().reloadScriptingInstance(retVal, ScriptingConst.ARTIFACT_TYPE_MANAGEDBEAN);

            if (newRetVal != retVal) {
                setValue(elContext, base, property, newRetVal);
            }

            return newRetVal;

        }*/

        return retVal;
    }




    public Class<?> getType(ELContext elContext, Object o, Object o1) throws NullPointerException, ELException {
        Class<?> retVal = _delegate.getType(elContext, o, o1);
        if (retVal != null && WeavingContext.isDynamic(retVal)) {
            return WeavingContext.getWeaver().reloadScriptingClass(retVal);
        }
        return retVal;
    }

    public void setValue(ELContext elContext, Object base, Object property, Object value) throws NullPointerException, ELException {
        //now to more complex relations...
        if (base != null && WeavingContext.isDynamic(base.getClass()) && WeavingContext.isDynamic(value.getClass())) {
            WeavingContext.getRefreshContext().getDependencyRegistry().addDependency(ScriptingConst.ENGINE_TYPE_JSF_ALL, base.getClass().getName(), base.getClass().getName(), value.getClass().getName());
        }
        _delegate.setValue(elContext, base, property, value);
    }

    public boolean isReadOnly(ELContext elContext, Object o, Object o1) throws NullPointerException, ELException {
        return _delegate.isReadOnly(elContext, o, o1);
    }

    public Iterator getFeatureDescriptors(ELContext elContext, Object o) {
        return _delegate.getFeatureDescriptors(elContext, o);
    }

    public Class<?> getCommonPropertyType(ELContext elContext, Object o) {
        return _delegate.getCommonPropertyType(elContext, o);
    }


    public ELResolverProxy() {
        _delegate = FacesContext.getCurrentInstance().getELContext().getELResolver();
    }

    public ELResolverProxy(ELResolver delegate) {
        _delegate = delegate;
    }

    public Object getDelegate() {
        return _delegate;
    }


   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
   {
     // our "pseudo-constructor"
     in.defaultReadObject();
     log = Logger.getLogger(ELResolverProxy.class.getName());

   }

}
