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
package org.apache.myfaces.extensions.scripting.jsf.annotation;

import org.apache.myfaces.config.element.ManagedBean;
import org.apache.myfaces.extensions.scripting.core.common.util.ReflectUtil;

/**
 * version dependend factory handler for the underlying ManagedBean implementation
 * (ther was an api change by Leo regarding the managed bean implementation)
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public class ManagedBeanHandler {

    final static String NEW_CLASS = "org.apache.myfaces.config.impl.digester.elements.ManagedBeanImpl";
    final static String OLD_CLASS = "org.apache.myfaces.config.impl.digester.elements.ManagedBean";

    public static ManagedBean newInstance() {
        try {
            if (MYFACE_VERSION < 2.2) {
                return (ManagedBean) Class.forName(OLD_CLASS).newInstance();
            } else {
                return (ManagedBean) Class.forName(NEW_CLASS).newInstance();
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static void setBeanClass(ManagedBean mbean, String name) {
        ReflectUtil.executeMethod(mbean, "setBeanClass", name);
    }

    public static void setName(ManagedBean mbean, String name) {
        ReflectUtil.executeMethod(mbean, "setName", name);
    }

    public static void setScope(ManagedBean mbean, String scope) {
        ReflectUtil.executeMethod(mbean, "setScope", scope);
    }

    public static double MYFACE_VERSION = getVersion();

    static double getVersion() {
        try {
            Class clazz = Class.forName(NEW_CLASS);
            return 2.2;
        } catch (ClassNotFoundException e) {
            return 2.1;
        }
    }

}
