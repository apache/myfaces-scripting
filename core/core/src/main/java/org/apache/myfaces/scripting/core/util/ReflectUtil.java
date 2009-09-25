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
package org.apache.myfaces.scripting.core.util;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class ReflectUtil {

    static Log log = LogFactory.getLog(ReflectUtil.class);

    public static Object instantiate(String clazz, Object... varargs) {
        return instantiate(ClassUtils.forName(clazz), varargs);
    }

    /**
     * A simplified instantiation over reflection
     *
     * @param clazz   the class to be istantiated
     * @param varargs the instantiation parameters
     * @return the instantiated object
     */
    public static Object instantiate(Class clazz, Object... varargs) {
        Class[] classes = new Class[varargs.length];
        for (int cnt = 0; cnt < varargs.length; cnt++) {

            if (varargs[cnt] instanceof Cast) {
                classes[cnt] = ((Cast) varargs[cnt]).getClazz();
                varargs[cnt] = ((Cast) varargs[cnt]).getValue();
            } else {
                classes[cnt] = varargs[cnt].getClass();
            }
        }

        Constructor constr = null;
        try {
            constr = clazz.getConstructor(classes);
            return (Object) constr.newInstance(varargs);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }/*this is mostly just a helper to bypass a groovy bug in a more
   * complex delegation environemt. Groovy throws a classcast
   * exeption wrongly, delegating the instantiation code to java
   * fixes that
   * */

    public static Object newObject(Class clazz) throws IllegalAccessException, InstantiationException {
        return clazz.newInstance();
    }


    /**
     * Generic execute method which simplifies the reflection api
     * down to a usable system
     *
     * @param obj        the target object the method has to be executed upon
     * @param methodName the method name
     * @param varargs    the arguments which have to be passed to the method
     * @return the return value of the method
     */
    public static Object executeStaticMethod(Class obj, String methodName, Object... varargs) {

        Collection<Method> methods = getMethods(obj, methodName, varargs.length);

        Object retVal = handleStaticMethod(obj, methodName, methods, varargs);
        if (!methodNotFound(retVal)) {
            return retVal;
        }

        methods = getAllMethods(obj, methodName, varargs.length);
        retVal = handleStaticMethod(obj, methodName, methods, varargs);
        if (!methodNotFound(retVal)) {
            return retVal;
        }

        throw new RuntimeException("Static Method of :" + methodName + " from class " + obj.getClass().getName() + " not found");

    }


    public static Collection<Method> getAllMethods(Class clazz, String methodName, int varargLength) {
        ArrayList<Method> retVal = new ArrayList<Method>(30);
        while (clazz.equals(java.lang.Object.class)) {
            for (Method m : clazz.getDeclaredMethods()) {
                if (m.getParameterTypes().length == varargLength && m.getName().equals(methodName)) {
                    retVal.add(m);
                }
            }
            clazz = clazz.getSuperclass();
        }

        return retVal;
    }


    public static Collection<Method> getMethods(Class clazz, String methodName, int varargLength) {
        ArrayList<Method> retVal = new ArrayList<Method>(30);
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.getParameterTypes().length == varargLength && m.getName().equals(methodName)) {
                retVal.add(m);
            }
        }

        return retVal;
    }


    /**
     * Generic execute method which simplifies the reflection api
     * down to a usable system
     *
     * @param obj        the target object the method has to be executed upon
     * @param methodName the method name
     * @param varargs    the arguments which have to be passed to the method
     * @return the return value of the method
     * @throws a generic runtime exception in case of a failure
     *           we use unmanaged exceptions here to get a behavior similar to scripting
     *           language execution where failures can happen but method executions
     *           should not enforce exception handling
     */
    public static Object executeMethod(Object obj, String methodName, Object... varargs) {

        Collection<Method> methods = null;
        //if we have an invocationHandler here we
        //can work over the generic invoke interface
        //That way we can cover more dynamic stuff
        //our reload invocation handler is treated differently here

        if (obj instanceof InvocationHandler) {
            InvocationHandler objToInvoke = (InvocationHandler) obj;

            Object realTarget = ProxyUtils.getDelegateFromProxy(objToInvoke);

            //first we try only the public because they are the most likely ones
            //to be accessed
            methods = getMethods(realTarget.getClass(), methodName, varargs.length);
            Object retVal = handleInvHandlerMethod(objToInvoke, methodName, methods, varargs);
            if (!methodNotFound(retVal)) {
                return retVal;
            }
            //if not we try all of them until we have a match
            methods = getAllMethods(realTarget.getClass(), methodName, varargs.length);
            retVal = handleInvHandlerMethod(objToInvoke, methodName, methods, varargs);
            if (!(methodNotFound(retVal))) {
                return retVal;
            }

            throw new RuntimeException("Method of :" + methodName + " from class " + obj.getClass().getName() + " not found");
        }

        Class clazz = obj.getClass();

        //first we try only the public because they are the most likely ones
        //to be accessed
        methods = getMethods(clazz, methodName, varargs.length);
        Object retVal = handleObjMethod(obj, methodName, methods, varargs);
        if (!methodNotFound(retVal)) {
            return retVal;
        }

        //if not we try all of them until we have a match
        methods = getAllMethods(clazz, methodName, varargs.length);
        retVal = handleObjMethod(obj, methodName, methods, varargs);
        if (!methodNotFound(retVal)) {
            return retVal;
        }

        throw new RuntimeException("Method of :" + methodName + " from class " + obj.getClass().getName() + " not found");
    }

    /**
     * special marker class which is a special return value indicating
     * that not method has been found which can be executed
     */
    static class _MethodNotFound {
    }


    /**
     * check if the return vaue is a method not found return val which
     * indicates we have to follow the next workflow step
     *
     * @param retVal
     * @return
     */
    private static boolean methodNotFound(Object retVal) {
        return retVal instanceof _MethodNotFound;
    }


    /**
     * executes a method in an invocation handler
     *
     * @param objToInvoke
     * @param methodName
     * @param methods
     * @param varargs
     * @return
     */
    static private Object handleInvHandlerMethod(InvocationHandler objToInvoke, String methodName, Collection<Method> methods, Object... varargs) {
        for (Method m : methods) {
            if (!m.getName().equals(methodName) || m.getParameterTypes().length != varargs.length) {
                continue;
            }
            try {
                return objToInvoke.invoke(objToInvoke, m, varargs);
            } catch (Throwable e) {
                handleException(e);
            }
        }
        return new _MethodNotFound();
    }

    /**
     * executes a method on an object
     *
     * @param objToInvoke
     * @param methodName
     * @param methods
     * @param varargs
     * @return
     */
    static private Object handleObjMethod(Object objToInvoke, String methodName, Collection<Method> methods, Object... varargs) {
        for (Method m : methods) {
            if (!m.getName().equals(methodName) || m.getParameterTypes().length != varargs.length) {
                continue;
            }
            try {
                return m.invoke(objToInvoke, varargs);
            } catch (Throwable e) {
                handleException(e);
            }
        }
        return new _MethodNotFound();
    }


    /**
     * executes a static method on a class
     *
     * @param objToInvoke
     * @param methodName
     * @param methods
     * @param varargs
     * @return
     */
    static private Object handleStaticMethod(Class objToInvoke, String methodName, Collection<Method> methods, Object... varargs) {
        for (Method m : methods) {
            if (!m.getName().equals(methodName) || m.getParameterTypes().length != varargs.length) {
                continue;
            }
            try {
                return m.invoke(objToInvoke, varargs);
            } catch (Throwable e) {
                handleException(e);
            }
        }
        return new _MethodNotFound();
    }


    private static void handleException(Throwable e) {
        if (e instanceof IllegalAccessException) {
            //do nothing
        } else if (e instanceof IllegalArgumentException) {
           //do nothing
        } else {
            throw new RuntimeException(e);
        }
    }


    /**
     * executes a function method on a target object
     *
     * @param obj        the target object
     * @param methodName the method name
     * @param varargs    a list of objects casts or nulls defining the parameter classes and its values
     *                   if something occurs on introspection level an unmanaged exception is throw, just like
     *                   it would happen in a scripting class
     * @return the result object for the Method(method) call
     * @throws RuntimeException an unmanaged runtime exception in case of an introspection error
     */
    public static Object fastExecuteMethod(Object obj, String methodName, Object... varargs) {
        Class[] classes = new Class[varargs.length];
        for (int cnt = 0; cnt < varargs.length; cnt++) {

            if (varargs[cnt] instanceof Cast) {
                classes[cnt] = ((Cast) varargs[cnt]).getClazz();
                varargs[cnt] = ((Cast) varargs[cnt]).getValue();
            } else {
                classes[cnt] = varargs[cnt].getClass();
            }
        }

        try {
            Method m = fastGetMethod(obj, methodName, classes);
            return m.invoke(obj, varargs);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * faster reflection call
     * if we know the data types excactly we can
     * trigger a direct call instead of walking through all methods
     *
     * @param obj
     * @param methodName
     * @param classes
     * @return
     * @throws NoSuchMethodException
     */
    public static Method fastGetMethod(Object obj, String methodName, Class[] classes) throws NoSuchMethodException {
        Method m = null;
        //TODO add inheritance handling here
        try {
            m = obj.getClass().getDeclaredMethod(methodName, classes);
        } catch (NoSuchMethodException e) {
            m = obj.getClass().getMethod(methodName, classes);
        }
        return m;
    }

    /**
     * executes a function method on a target object
     *
     * @param obj        the target object
     * @param methodName the method name
     * @param varargs    a list of objects casts or nulls defining the parameter classes and its values
     *                   if something occurs on introspection level an unmanaged exception is throw, just like
     *                   it would happen in a scripting class
     * @return the result object for the Method(method) call
     * @throws RuntimeException an unmanaged runtime exception in case of an introspection error
     */
    public static Object fastExecuteStaticMethod(Class obj, String methodName, Object... varargs) {
        Class[] classes = new Class[varargs.length];
        for (int cnt = 0; cnt < varargs.length; cnt++) {

            if (varargs[cnt] instanceof Cast) {
                classes[cnt] = ((Cast) varargs[cnt]).getClazz();
                varargs[cnt] = ((Cast) varargs[cnt]).getValue();
            } else {
                classes[cnt] = varargs[cnt].getClass();
            }
        }

        //TODO add autocasting instead of manual casting

        try {
            Method m = fastGetStaticMethod(obj, methodName, classes);
            return m.invoke(obj, varargs);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    public static Method fastGetStaticMethod(Class obj, String methodName, Class[] classes) throws NoSuchMethodException {
        Method m = null;
        try {
            m = obj.getDeclaredMethod(methodName, classes);
        } catch (NoSuchMethodException e) {
            m = obj.getMethod(methodName, classes);
        }
        return m;
    }

    /**
     * convenience method which makes the code a little bit more readable
     * use it in conjunction with static imports
     *
     * @param clazz the cast target for the method call
     * @param value the value object to be used as param
     * @return a Cast object of the parameters
     */
    public static Cast cast(Class clazz, Object value) {
        return new Cast(clazz, value);
    }

    /**
     * convenience method which makes the code a little bit more readable
     * use it in conjunction with static imports
     *
     * @param clazz the cast target for the method call
     * @return a null value Cast object of the parameters
     */
    public static Null nullCast(Class clazz) {
        return new Null(clazz);
    }
}
