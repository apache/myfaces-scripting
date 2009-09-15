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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class ReflectUtil {
    public static Object instantiate(String clazz, Object... varargs) {
        return instantiate(ClassUtils.forName(clazz), varargs);
    }

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


    public static Object executeStaticMethod(Class obj, String methodName, Object... varargs) {

        Method[] methods = obj.getDeclaredMethods();

         for (Method m : methods) {

            if (!m.getName().equals(methodName) || m.getParameterTypes().length != varargs.length) {
                continue;
            }
            try {
                return m.invoke(obj, varargs);
            } catch (IllegalAccessException e) {
                //expected, we do it that way for speed reasons
                //because it is faster on most cases than looping over the param types
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        methods = obj.getMethods();
        for (Method m : methods) {
            if (!m.getName().equals(methodName) || m.getParameterTypes().length != varargs.length) {
                continue;
            }
            try {
                return m.invoke(obj, varargs);
            } catch (IllegalAccessException e) {
                //expected, we do it that way for speed reasons
                //because it is faster on most cases than looping over the param types
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        throw new RuntimeException("Static Method of :" + methodName + " from class " + obj.getClass().getName() + " not found");

    }







    public static Object executeMethod(Object obj, String methodName, Object... varargs) {
        Class clazz = obj.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method m : methods) {
            if (!m.getName().equals(methodName) || m.getParameterTypes().length != varargs.length) {
                continue;
            }
            try {
                return m.invoke(obj, varargs);
            } catch (IllegalAccessException e) {
                //expected, we do it that way for speed reasons
                //because it is faster on most cases than looping over the param types
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        methods = clazz.getMethods();
        for (Method m : methods) {
            if (!m.getName().equals(methodName) || m.getParameterTypes().length != varargs.length) {
                continue;
            }
            try {
                return m.invoke(obj, varargs);
            } catch (IllegalAccessException e) {
                //expected, we do it that way for speed reasons
                //because it is faster on most cases than looping over the param types
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("Method of :" + methodName + " from class " + obj.getClass().getName() + " not found");
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


    public static Method fastGetMethod(Object obj, String methodName, Class[] classes) throws NoSuchMethodException {
        Method m = null;
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
