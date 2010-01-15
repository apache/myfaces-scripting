package org.apache.myfaces.scripting.loaders.groovy;

import org.apache.myfaces.scripting.loaders.java.RecompiledClassLoader;

/**
 * Created by IntelliJ IDEA.
 * User: werpu2
 * Date: 12.01.2010
 * Time: 20:08:18
 * To change this template use File | Settings | File Templates.
 */
public class GroovyRecompiledClassloader extends RecompiledClassLoader {
    public GroovyRecompiledClassloader(ClassLoader classLoader, int scriptingEngine, String engineExtension) {
        super(classLoader, scriptingEngine, engineExtension);
    }
}
