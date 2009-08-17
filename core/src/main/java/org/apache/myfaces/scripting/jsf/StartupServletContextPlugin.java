package org.apache.myfaces.scripting.jsf;

import org.apache.myfaces.groovyloader.core.DelegatingGroovyClassloader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContext;

/**
 * Servlet context plugin which provides a cleaner initialisation
 * than the standard servlet context
 *
 * @author Werner Punz
 */
public class StartupServletContextPlugin implements ServletContextListener {
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        Log log = LogFactory.getLog(this.getClass());

        log.info("Instantiating StartupServletContextPluginChainLoader");

        ServletContext servletContext = servletContextEvent.getServletContext();
        if (servletContext == null) return;



        /*
        if (!(Thread.currentThread().getContextClassLoader() instanceof DelegatingGroovyClassloader)) {
            ClassLoader newLoader = null;

            newLoader = new DelegatingGroovyClassloader(servletContext);
            Thread.currentThread().setContextClassLoader(newLoader);
            servletContext.setAttribute(ScriptingConst.SCRIPTING_CLASSLOADER, newLoader);
        } */
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }

}
