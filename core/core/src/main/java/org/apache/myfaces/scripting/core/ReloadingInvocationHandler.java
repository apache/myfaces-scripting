package org.apache.myfaces.scripting.core;

import org.apache.myfaces.scripting.api.Decorated;
import org.apache.myfaces.scripting.core.util.ReflectUtil;

import java.lang.reflect.InvocationHandler;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          We set our own invocation handler
 *          here to allow reflection utils directly targetting our
 *          _delegate.
 */
public abstract class ReloadingInvocationHandler implements InvocationHandler, Decorated {
    Class _loadedClass = null;
    Object _delegate = null;

    /**
     * simplified invoke for more dynamic upon invocation
     * on our reloading objects
     *
     * @param o
     * @param m
     * @param args
     * @return
     */
    public Object invoke(Object o, String m, Object... args) {
        return ReflectUtil.executeMethod(o, m, args);
    }


    public Class getLoadedClass() {
        return _loadedClass;
    }

    public Object getDelegate() {
        return _delegate;
    }

    public void setDelegate(Object delegate) {
        _delegate = delegate;
    }

    public void setLoadedClassName(Class loadedClass) {
        this._loadedClass = loadedClass;
    }

}
