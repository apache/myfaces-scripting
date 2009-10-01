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

import java.beans.FeatureDescriptor;
import java.util.*;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.PropertyNotFoundException;
import javax.el.PropertyNotWritableException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.scripting.api.Decorated;
import org.apache.myfaces.scripting.core.util.ProxyUtils;
import org.apache.myfaces.scripting.core.scanEvents.SystemEventListener;
import org.apache.myfaces.scripting.core.scanEvents.SystemEvent;
import org.apache.myfaces.scripting.core.scanEvents.events.BeanLoadedEvent;
import org.apache.myfaces.scripting.core.scanEvents.events.BeanRemovedEvent;

/**
 * EL Resolver which is scripting enabled
 *
 * @author Werner Punz
 */
public class ELResolverProxy extends ELResolver implements Decorated {

    Log log = LogFactory.getLog(ELResolverProxy.class);
    ELResolver _delegate = null;

    StackingBeanEventListener _listenerStack = new StackingBeanEventListener();

    private void registerListenerStack() {
        if (!ProxyUtils.getEventProcessor().hasListener(_listenerStack)) {
            ProxyUtils.getEventProcessor().addListener(_listenerStack);
        }
    }


    public Object getValue(ELContext elContext, final Object base, final Object property) throws NullPointerException, PropertyNotFoundException, ELException {
        Object retVal = _delegate.getValue(elContext, base, property);
        Object newRetVal = null;
        if (retVal != null && ProxyUtils.isDynamic(retVal.getClass())) {

            //We have to register one listener per el instance
            //because we have to deal with nesting of
            //el locading caused by beans referencing other beans
            //via inversion of object control

            //onfurtunately this flow is somewhat strange because
            //we shift logic into dynamically registered events
            //but the event system is the only means of communication
            //attached modules have to the core

            //the flow is this -> try to load bean bean loading failes because
            //annotation has moved -> this is notified about it ->
            //we have to do a full recompile
            //retry to load but this time without our referencing listener
            registerListenerStack();
            BeanEventListener eventListener = new BeanEventListener();
            _listenerStack.pushListener(eventListener);
            try {
                //We register for annotation scanning events

                newRetVal = ProxyUtils.getWeaver().reloadScriptingInstance(retVal); /*once it was tainted or loaded by
                 our classloader we have to recreate all the time to avoid classloader issues*/
                fullRecompileReload(eventListener);
            } finally {
                _listenerStack.popListener();
            }

            if (newRetVal != retVal) {
                _delegate.setValue(elContext, base, property, newRetVal);
            }

            return newRetVal;
            //reinstantiated.put(retVal.getClass().getName(), retVal.getClass());
        }

        return retVal;

    }


    private void fullRecompileReload(BeanEventListener eventListener) {
        if (eventListener.getBeanEventIssued().size() > 0) {
            //last was the first event issued
            SystemEvent beanEvent = eventListener.getBeanEventIssued().removeLast();
            eventListener.getBeanEventIssued().clear();
            if (beanEvent instanceof BeanRemovedEvent) {

                if (log.isDebugEnabled()) {
                    log.debug("ElResolverProxy.getValue bean removed we have to issue a recompile and then try to load the bean anew");

                }
            }
        }
    }

    public Class<?> getType(ELContext elContext, Object o, Object o1) throws NullPointerException, PropertyNotFoundException, ELException {
        Class<?> retVal = _delegate.getType(elContext, o, o1);
        if (retVal != null && ProxyUtils.isDynamic((Class) retVal)) {
            return ProxyUtils.getWeaver().reloadScriptingClass((Class) retVal);
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


    public Object getDelegate() {
        return _delegate;  //To change body of implemented methods use File | Settings | File Templates.
    }


    //TODO replace the stack with a direct check for the bean name,
    //this should be sufficient and should make the code much easier to deal with
    /**
     * We stack our event listeners, because only the one currently
     * active should receive the bean events
     * This is because the IOC Resolving can happen during reload
     * and only the current level within the object
     * resolution should trigger the recompile and reload!
     */
    class StackingBeanEventListener implements SystemEventListener {
        private LinkedList<BeanEventListener> _stack = new LinkedList<BeanEventListener>();


        public void pushListener(BeanEventListener listener) {
            _stack.addFirst(listener);
        }

        public BeanEventListener popListener() {
            return _stack.removeFirst();
        }

        public Set<Integer> supportsEvents() {
            if (_stack.isEmpty()) {
                return Collections.EMPTY_SET;
            }
            return _stack.getFirst().supportsEvents();  //To change body of implemented methods use File | Settings | File Templates.
        }

        public void handleEvent(SystemEvent evt) {
            if (_stack.isEmpty()) {
                return;
            }
            _stack.getFirst().handleEvent(evt);
        }
    }


    class BeanEventListener implements SystemEventListener {

        private LinkedList<SystemEvent> _beanEventIssued = new LinkedList<SystemEvent>();
        //Map reinstantiated = new HashMap();


        BeanEventListener() {
        }

        public Set<Integer> supportsEvents() {
            Set<Integer> supports = new HashSet<Integer>();
            supports.add(BeanLoadedEvent.ARTEFACT_TYPE_MANAGEDBEAN);

            return supports;  //To change body of implemented methods use File | Settings | File Templates.
        }

        /**
         * bean loaded and unloaded can trigger an applicationState
         *
         * @param evt
         */
        public void handleEvent(SystemEvent evt) {
            if (evt instanceof BeanLoadedEvent || evt instanceof BeanRemovedEvent) {
                _beanEventIssued.addFirst(evt);
            }
        }


        public LinkedList<SystemEvent> getBeanEventIssued() {
            return _beanEventIssued;
        }

        public void setBeanEventIssued(LinkedList<SystemEvent> beanEventIssued) {
            _beanEventIssued = beanEventIssued;
        }
    }
}
