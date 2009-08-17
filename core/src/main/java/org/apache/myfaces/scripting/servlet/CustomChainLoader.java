package org.apache.myfaces.scripting.servlet;

import org.apache.myfaces.groovyloader.core.GroovyWeaver;
import org.apache.myfaces.shared_impl.util.ClassLoaderExtension;

import javax.servlet.ServletContext;
import java.io.File;

public class CustomChainLoader extends ClassLoaderExtension {

    String _classRoot = "";
    String _groovyRoot = "";
    String _testRoot = "/Users/werpu/Desktop/myfaces-groovy/core/src/main/groovy/";
    static GroovyWeaver groovyWeaver = null;

    static public GroovyWeaver getGroovyFactory() {
        return groovyWeaver;
    }

    static public void setGroovyFactory(GroovyWeaver groovyWeaver) {
        groovyWeaver = groovyWeaver;
    }


    public CustomChainLoader(ServletContext servletContext) {
        this.groovyWeaver = new GroovyWeaver();
          String contextRoot = servletContext.getRealPath("/WEB-INF/groovy/");

          contextRoot = contextRoot.trim();
          if(!contextRoot.endsWith("/") && !contextRoot.endsWith("\\"))
              contextRoot += "/";
          _groovyRoot = contextRoot;

    }

    public Class forName(String name) {

        if (name.startsWith("java.")) /*the entire java namespace is reserved so no use to do a specific classloading check here*/
            return null;
        else if (name.startsWith("com.sun")) /*internal java specific namespace*/
            return null;

        String groovyClass = name.replaceAll("\\.", "/") + ".groovy";
        File classFile = new File(_groovyRoot + groovyClass);

        if (classFile.exists()) /*we check the groovy subdir for our class*/
            return (Class) groovyWeaver.loadScriptingClassFromFile(_groovyRoot + groovyClass);

        classFile = new File(_classRoot + groovyClass);
        if (classFile.exists()) /*now lets check our class subdir for a groovy file to be loaded*/
            return (Class) groovyWeaver.loadScriptingClassFromFile(_classRoot + groovyClass);

        /*standard testcase*/
        classFile = new File(_testRoot + groovyClass);


        if (classFile.exists()) /*now lets check our class subdir for a groovy file to be loaded*/
            return groovyWeaver.loadScriptingClassFromFile(_testRoot + groovyClass);


        return null;
    }
}