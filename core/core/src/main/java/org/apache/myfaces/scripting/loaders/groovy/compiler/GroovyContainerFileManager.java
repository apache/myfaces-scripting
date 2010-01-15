package org.apache.myfaces.scripting.loaders.groovy.compiler;

import org.apache.myfaces.scripting.api.ScriptingConst;
import org.apache.myfaces.scripting.core.util.ClassUtils;
import org.apache.myfaces.scripting.loaders.groovy.GroovyRecompiledClassloader;
import org.apache.myfaces.scripting.loaders.java.RecompiledClassLoader;
import org.apache.myfaces.scripting.loaders.java.jdk5.ContainerFileManager;

/**
 * Created by IntelliJ IDEA.
 * User: werpu2
 * Date: 12.01.2010
 * Time: 18:19:17
 * To change this template use File | Settings | File Templates.
 */
public class GroovyContainerFileManager extends ContainerFileManager {

    public void refreshClassloader() {
         classLoader = new GroovyRecompiledClassloader(ClassUtils.getContextClassLoader(), ScriptingConst.ENGINE_TYPE_GROOVY, ".groovy");
     }

}
