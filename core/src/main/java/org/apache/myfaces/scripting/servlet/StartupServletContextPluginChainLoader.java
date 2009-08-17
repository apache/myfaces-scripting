package org.apache.myfaces.scripting.servlet;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.shared_impl.util.ClassUtils;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContext;

/**
 * @author werpu
 * @date: 14.08.2009
 */
public class StartupServletContextPluginChainLoader implements ServletContextListener {
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        Log log = LogFactory.getLog(this.getClass());

        log.info("Instantiating StartupServletContextPluginChainLoader");

        ServletContext servletContext = servletContextEvent.getServletContext();
        if (servletContext == null) return;

        CustomChainLoader loader = new CustomChainLoader(servletContext);
        ClassUtils.addClassLoadingExtension(loader, true);
        servletContext.setAttribute("GroovyDynamicLoader", loader.getGroovyFactory());

   }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }

}