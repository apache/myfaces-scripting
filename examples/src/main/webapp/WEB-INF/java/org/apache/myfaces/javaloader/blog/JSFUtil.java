package org.apache.myfaces.javaloader.blog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.shared_impl.util.ClassUtils;

import javax.faces.context.FacesContext;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;


/**
 * @author werpu2
 * @date: 01.09.2009
 * <p/>
 * A helper for JSF and introspection related tasks
 */
public class JSFUtil {

    public JSFUtil() {
    }

    /**
     * resolves a variable in the current facesContext
     *
     * @param beanName
     * @return
     */
    public static Object resolveVariable(String beanName) {
        Log log = LogFactory.getLog(JSFUtil.class);
        Object facesContext = FacesContext.getCurrentInstance();

        Object elContext = executeFunction(facesContext, "getELContext");
        Object elResolver = executeFunction(elContext, "getELResolver");

        log.info("ElResolver Instance:" + elResolver.toString());
        try {
            return executeFunction(elResolver, "getValue", cast(ClassUtils.classForName("javax.el.ELContext"), elContext), nullCast(Object.class), cast(Object.class, beanName));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * executes a method
     *
     * @param obj        the target object
     * @param methodName the method name
     * @param varargs    a list of objects casts or nulls defining the parameter classes and its values
     *                   if something occurs on introspection level an unmanaged exception is throw, just like
     *                   it would happen in a scripting class
     */
    public static void executeMethod(Object obj, String methodName, Object... varargs) {

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
            Method m = obj.getClass().getMethod(methodName, classes);
            m.invoke(obj, varargs);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * executes a function on a target object
     *
     * @param obj        the target object
     * @param methodName the method name
     * @param varargs    a list of objects casts or nulls defining the parameter classes and its values
     *                   if something occurs on introspection level an unmanaged exception is throw, just like
     *                   it would happen in a scripting class
     * @return the result object for the function(method) call
     * @throws RuntimeException an unmanaged runtime exception in case of an introspection error
     */
    public static Object executeFunction(Object obj, String methodName, Object... varargs) {
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
            Method m = obj.getClass().getMethod(methodName, classes);
            return m.invoke(obj, varargs);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }


    public static Cast cast(Class clazz, Object value) {
        return new Cast(clazz, value);
    }

    public static Null nullCast(Class clazz) {
        return new Null(clazz);
    }

}
