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
package org.apache.myfaces.scripting.jsf2.annotation;

import org.apache.myfaces.config.RuntimeConfig;
import org.apache.myfaces.config.element.NavigationRule;
import org.apache.myfaces.config.impl.digester.elements.ManagedBean;
import org.apache.myfaces.scripting.api.AnnotationScanListener;
import org.apache.myfaces.scripting.core.util.WeavingContext;

import javax.faces.bean.*;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          bean implementation listener which registers new java sources
 *          into the runtime config, note this class is not thread safe
 *          it is only allowed to be called from a single thread
 */

public class BeanImplementationListener extends BaseAnnotationScanListener implements AnnotationScanListener {

    private static final String SCOPE_SESSION = "session";
    private static final String SCOPE_APPLICATION = "application";
    private static final String SCOPE_VIEW = "view";
    private static final String SCOPE_NONE = "none";
    private static final String SCOPE_CUSTOM = "custom";


    public boolean supportsAnnotation(String annotation) {
        return annotation.equals(javax.faces.bean.ManagedBean.class.getName());
    }

    public void register(Class clazz, java.lang.annotation.Annotation ann) {
        String annotationName = ann.getClass().getName();

        RuntimeConfig config = getRuntimeConfig();

        javax.faces.bean.ManagedBean annCasted = (javax.faces.bean.ManagedBean) ann;

        String beanName = annCasted.name();


        beanName = beanName.replaceAll("\"", "");
        if (!hasToReregister(beanName, clazz)) {
            return;
        }

        ManagedBean mbean = new ManagedBean();
        mbean.setBeanClass(clazz.getName());
        mbean.setName(beanName);
        handleManagedpropertiesCompiled(mbean, fields(clazz));
        resolveScope(clazz, mbean);

        _alreadyRegistered.put(beanName, mbean);
        config.addManagedBean(beanName, mbean);

    }

    private void resolveScope(Class clazz, ManagedBean mbean) {
        //now lets resolve the scope
        String scope = "none";
        if (clazz.isAnnotationPresent(RequestScoped.class)) {
            scope = "request";
        } else if (clazz.isAnnotationPresent(SessionScoped.class)) {
            scope = "session";
        } else if (clazz.isAnnotationPresent(ApplicationScoped.class)) {
            scope = "application";
        } else if (clazz.isAnnotationPresent(NoneScoped.class)) {
            scope = "none";
        } else if (clazz.isAnnotationPresent(CustomScoped.class)) {
            scope = "custom";
        }
        mbean.setScope(scope);
    }


    private void handleManagedpropertiesCompiled(ManagedBean mbean, Field[] fields) {
        for (Field field : fields) {
            if (log.isTraceEnabled()) {
                log.trace("  Scanning field '" + field.getName() + "'");
            }
            javax.faces.bean.ManagedProperty property = (javax.faces.bean.ManagedProperty) field
                    .getAnnotation(javax.faces.bean.ManagedProperty.class);
            if (property != null) {
                if (log.isDebugEnabled()) {
                    log.debug("  Field '" + field.getName()
                            + "' has a @ManagedProperty annotation");
                }

                org.apache.myfaces.config.impl.digester.elements.ManagedProperty mpc =
                        new org.apache.myfaces.config.impl.digester.elements.ManagedProperty();
                String name = property.name();
                if ((name == null) || "".equals(name)) {
                    name = field.getName();
                }
                mpc.setPropertyName(name);
                mpc.setPropertyClass(field.getType().getName()); // FIXME - primitives, arrays, etc.
                mpc.setValue(property.value());
                mbean.addProperty(mpc);

                continue;
            }
        }
    }


    /**
     * <p>Return an array of all <code>Field</code>s reflecting declared
     * fields in this class, or in any superclass other than
     * <code>java.lang.Object</code>.</p>
     *
     * @param clazz Class to be analyzed
     */
    private Field[] fields(Class clazz) {

        Map<String, Field> fields = new HashMap<String, Field>();
        do {
            for (Field field : clazz.getDeclaredFields()) {
                if (!fields.containsKey(field.getName())) {
                    fields.put(field.getName(), field);
                }
            }
        } while ((clazz = clazz.getSuperclass()) != Object.class);
        return (Field[]) fields.values().toArray(new Field[fields.size()]);
    }


    protected boolean hasToReregister(String name, Class clazz) {
        ManagedBean mbean = (ManagedBean) _alreadyRegistered.get(name);
        return mbean == null || !mbean.getManagedBeanClassName().equals(clazz.getName());
    }


    @SuppressWarnings("unchecked")
    public void purge(String className) {
        RuntimeConfig config = getRuntimeConfig();
        //We have to purge and readd our managed beans, unfortunatly the myfaces impl enforces
        //us to do the same for the nav rules after purge
        //we cannot purge the managed beans and nav rules separately
        Collection<NavigationRule> navigationRules = new ArrayList<NavigationRule>();
        Map<String, org.apache.myfaces.config.element.ManagedBean> managedBeans = new HashMap<String, org.apache.myfaces.config.element.ManagedBean>();

        navigationRules.addAll(config.getNavigationRules());
        managedBeans.putAll(config.getManagedBeans());

        config.purge();

        for (NavigationRule navRule : navigationRules) {
            config.addNavigationRule(navRule);
        }

        //We refresh the managed beans, dead references still can cause
        //runtime errors but in this case we cannot do anything
        org.apache.myfaces.config.element.ManagedBean mbeanFound = null;
        List<String> mbeanKey = new LinkedList<String>();

        for (Map.Entry mbean : managedBeans.entrySet()) {
            org.apache.myfaces.config.element.ManagedBean bean = (org.apache.myfaces.config.element.ManagedBean) mbean.getValue();
            if (!bean.getManagedBeanClass().getName().equals(className)) {
                config.addManagedBean((String) mbean.getKey(), (org.apache.myfaces.config.element.ManagedBean) mbean.getValue());
            } else {
                mbeanFound = (org.apache.myfaces.config.element.ManagedBean) mbean.getValue();
                mbeanKey.add(mbeanFound.getManagedBeanName());
            }
        }
        if (mbeanFound != null) {
            for (String toRemove : mbeanKey) {
                _alreadyRegistered.remove(toRemove);
            }
        }

    }
}
