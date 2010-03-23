package org.apache.myfaces.otherEngines;

import org.apache.commons.io.FileUtils;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This is only a demonstration application on how to implement
 * managed beans in other engines using the java scripting api
 * We do not really support it but feel free to apply the
 * techniques used here
 */
public class JavascriptProxyFactory implements InvocationHandler {

    static ScriptEngine _engine = null;

    static {
        ScriptEngineManager manager = new ScriptEngineManager();
        _engine = manager.getEngineByName("JavaScript");
    }

    static AtomicInteger _instanceIncr = new AtomicInteger(0);
    String _jsInstance;
    Object _jsProxy;
    String _script;

    protected JavascriptProxyFactory(String classDef, String script) throws ScriptException {
        int currCnt = _instanceIncr.getAndIncrement();
        _jsInstance = "myVar_" + currCnt;

        this._script = script + " var " + _jsInstance + " = new " + classDef + "();";
        _engine.eval(this._script);
        _jsProxy = _engine.get(_jsInstance);

    }

    public static synchronized Object newInstance(Class theInterface, String jsClass, File script) throws ScriptException {

        try {
            return java.lang.reflect.Proxy.newProxyInstance(theInterface.getClassLoader(), new Class[]{theInterface}, new JavascriptProxyFactory(jsClass, FileUtils.readFileToString(script)));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object... args) throws Throwable {
        Invocable inv = (Invocable) _engine;
        return inv.invokeMethod(_jsProxy, method.getName(), args);
    }
}
