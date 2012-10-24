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

package org.apache.myfaces.extensions.scripting.spring.util;

import org.springframework.aop.scope.ScopedObject;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Proxy;

/**
 *
 */
public class ProxyAopUtils {

    /**
     * <p>Returns true if and only if the given Class reference is either a JDK
     * dynamic proxy or a CGLIB proxy.</p>
     *
     * @param clazz the class to check
     * @see #isJdkProxyClass(Class)
     * @see #isCglibProxyClass(Class)
     */
    public static boolean isProxyClass(Class clazz) {
        return isJdkProxyClass(clazz) || isCglibProxyClass(clazz);
    }

    /**
     * <p>Returns true if and only if the specified class was dynamically
     * generated to be a proxy class using the builtin JDK dynamic proxy
     * facilities.</p>
     *
     * @param clazz the class to check
     */
    public static boolean isJdkProxyClass(Class clazz) {
        return clazz != null && Proxy.isProxyClass(clazz);
    }

    /**
     * <p>Returns true if and only if the specified class was dynamically
     * generated to be a proxy class using the CGLIB dynamic proxy
     * facilities.</p>
     *
     * @param clazz the class to check
     */
    public static boolean isCglibProxyClass(Class clazz) {
        return clazz != null && clazz.getName().contains("$$");
    }

    public static boolean isScopedProxy(Object obj) {
        if (isProxyClass(obj.getClass())) {
            return ProxyAopUtilities.isScopedObject(obj);
        }
        else {
            return false;
        }
    }

    /**
     * <p>Returns the target class of the given Class reference, i.e. if the
     * given Class reference is a dynamiccaly generated proxy class, its target
     * class will be returned (that is, the Class reference being proxied by
     * this one). Otherwise the given Class reference will be returned.</p>
     */
    public static Class resolveTargetClass(Object proxy) {
        if (isProxyClass(proxy.getClass())) {
            return ProxyAopUtilities.resolveTargetClass(proxy);
        }
        else {
            return proxy.getClass();
        }
    }

    // ------------------------------------------ Private static classes

    /**
     * <p>Private static class that knows how to deal with dynamically generated proxy.
     * Note that the implementation requires that Spring AOP is present on the classpath,
     * which is why a private static class has been introduced. In doing so, Spring AOP
     * is only required if we're definitely dealing with a proxy.</p>
     */
    private static class ProxyAopUtilities {

        /**
         * <p>Returns whether the given object is a scoped object, i.e. a proxy
         * delegating to a bean that resides within a certain scope.</p>
         */
        public static boolean isScopedObject(Object obj) {
            return obj instanceof ScopedObject;
        }

        /**
         * <p>Returns the target class of the given proxy Class reference, i.e.
         * the Class reference being proxied by this one.</p>
         */
        public static Class resolveTargetClass(Object proxy) {
            return AopUtils.getTargetClass(proxy);
        }

    }

}
