package org.apache.myfaces.scripting.api;

/**
 * @author werpu
 * @date: 15.08.2009
 */
public interface ScriptingWeaver {

    public Object reloadScriptingInstance(Object o);

    public Class reloadScriptingClass(Class aclass);

    public Class loadScriptingClassFromFile(String file);
}
