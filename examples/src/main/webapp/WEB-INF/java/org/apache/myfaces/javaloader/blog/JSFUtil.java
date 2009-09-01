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
 */
public class JSFUtil {

    public JSFUtil() {
    }

    public static Object resolveVariable(String beanName) {
        Log log = LogFactory.getLog(JSFUtil.class);
        Object facesContext = FacesContext.getCurrentInstance();

        Object elContext = executeFunction(facesContext, "getELContext");
        Object elResolver = executeFunction(elContext, "getELResolver");

        log.info("ElResolver Instance:" + elResolver.toString());
        try {
            return executeFunction(elResolver, "getValue", new Cast(ClassUtils.classForName("javax.el.ELContext"), elContext), new Null(Object.class), new Cast(Object.class, beanName));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }


    public static void executeMethod(Object obj, String methodName, Object... varargs) {
        
        Class[] classes = new Class[varargs.length];
        for (int cnt = 0; cnt < varargs.length; cnt++) {

            if (varargs[cnt] instanceof Null) {
                classes[cnt] = ((Null) varargs[cnt]).getNulledClass();
                varargs[cnt] = null;
            } else if (varargs[cnt] instanceof Cast) {
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

    public static Object executeFunction(Object obj, String methodName, Object... varargs) {
        Class[] classes = new Class[varargs.length];
        for (int cnt = 0; cnt < varargs.length; cnt++) {

            if (varargs[cnt] instanceof Null) {
                classes[cnt] = ((Null) varargs[cnt]).getNulledClass();
                varargs[cnt] = null;
            } else if (varargs[cnt] instanceof Cast) {
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

}
