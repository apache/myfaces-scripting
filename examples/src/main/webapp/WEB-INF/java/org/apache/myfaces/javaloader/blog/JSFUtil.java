package org.apache.myfaces.javaloader.blog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.shared_impl.util.ClassUtils;

import javax.faces.context.FacesContext;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import static org.apache.myfaces.scripting.core.util.ClassUtils.*;

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


   

}
