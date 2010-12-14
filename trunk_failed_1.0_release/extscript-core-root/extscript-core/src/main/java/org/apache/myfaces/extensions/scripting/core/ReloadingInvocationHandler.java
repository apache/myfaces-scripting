package org.apache.myfaces.extensions.scripting.core;

import org.apache.myfaces.extensions.scripting.api.Decorated;
import org.apache.myfaces.extensions.scripting.core.util.ReflectUtil;

import java.lang.reflect.InvocationHandler;

/**
 * <p/>
 * We set our own invocation handler
 * here to allow reflection utils directly targeting our
 * _delegate.
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
@SuppressWarnings("unused")
public abstract class ReloadingInvocationHandler implements InvocationHandler, Decorated {
    Class _loadedClass = null;
    Object _delegate = null;

    /**
     * simplified invoke for more dynamic upon invocation
     * on our reloading objects
     *
     * @param object    the object to be invoked on
     * @param method    the method to be invoked
     * @param arguments the arguments passed down
     * @return the return value of the operation
     */
    public Object invoke(Object object, String method, Object... arguments) {
        return ReflectUtil.executeMethod(object, method, arguments);
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
