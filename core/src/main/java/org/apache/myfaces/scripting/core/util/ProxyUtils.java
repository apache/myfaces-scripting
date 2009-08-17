package org.apache.myfaces.scripting.core.util;


import org.apache.myfaces.groovyloader.core.DelegatingGroovyClassloader;
import org.apache.myfaces.scripting.api.Decorated;
import org.apache.myfaces.scripting.api.ScriptingWeaver;
import org.apache.myfaces.scripting.core.util.MethodLevelReloadingHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * A set of proxy utils called
 * by the various subsystems
 * 
 * @author Werner Punz
 */
public class ProxyUtils {

    /**
     * we push our _weaver into the thread local
     * to avoid too many calls into the
     * context classloading hierarchy
     * this should speed things up a little bit.
     *
     * Note we could work with this with static
     * objects as well but since we also have to work with context
     * reloading we probably are safer with filters
     * a reference in the context and a threadLocal variable
     * 
     */
    static ThreadLocal _weaverHolder = new ThreadLocal();


    public static void init() {

    }

    public static void clean() {
        _weaverHolder.set(null);
    }

    public static void setWeaver(Object weaver) {
        _weaverHolder.set(weaver);
    }

    public static boolean isScriptingEnabled() {
        if (_weaverHolder.get() != null) {
            return true;
        } else {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            do {
                if (loader instanceof DelegatingGroovyClassloader)
                    return true;

                loader = loader.getParent();
            } while (loader != null);

            return false;
        }
    }

    public static ScriptingWeaver getWeaver() {
        if (_weaverHolder.get() == null) {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            while (loader != null && !(loader instanceof DelegatingGroovyClassloader)) {
                loader = loader.getParent();
            }
            if (loader != null) {
                _weaverHolder.set(((DelegatingGroovyClassloader) loader).getGroovyFactory());
            }
        }
        return (ScriptingWeaver) _weaverHolder.get();
    }

    /**
     * we create a proxy to an existing object
     * which does reloading of the internal class
     * on method level
     * 
     * @param o the source object to be proxied
     * @param theInterface the proxying interface
     * @return a proxied reloading object of type theInterface
     */
    public static Object createMethodReloadingProxyFromObject(Object o, Class theInterface) {
        return Proxy.newProxyInstance(o.getClass().getClassLoader(),
                                      new Class[]{theInterface},
                                      new MethodLevelReloadingHandler(o));
    }

    /**
     * we create a proxy to an existing object
     * which does reloading of the internal class
     * on newInstance level
     *
     * @param o
     * @param theInterface
     * @return
     */
    public static Object createConstructorReloadingProxyFromObject(Object o, Class theInterface) {
        return Proxy.newProxyInstance(o.getClass().getClassLoader(),
                                      new Class[]{theInterface},
                                      new MethodLevelReloadingHandler(o));
    }


    /**
     * unmapping of a proxied object
     * 
     * @param o the proxied object
     * @return the unproxied object
     */
    public static Object getDelegateFromProxy(Object o) {
        if (o instanceof Decorated)
            return ((Decorated) o).getDelegate();

        if (!Proxy.isProxyClass(o.getClass())) return o;
        InvocationHandler handler = Proxy.getInvocationHandler(o);
        if (handler instanceof Decorated) {
            return ((Decorated) handler).getDelegate();
        }
        return o;
    }
}
