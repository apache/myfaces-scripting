package org.apache.myfaces.extensions.scripting.cdi.context;

import org.apache.myfaces.extensions.scripting.cdi.compiler.CompilerFactory;
import org.apache.myfaces.extensions.scripting.cdi.loaders.DependencyAwareReloadingClassLoader;
import org.apache.myfaces.extensions.scripting.cdi.loaders.ReloadingClassLoader;
import org.apache.myfaces.extensions.scripting.cdi.loaders.dependency.scanner.asm.AsmClassReadingDependencyScanner;
import org.apache.myfaces.extensions.scripting.cdi.monitor.FileMonitoringTask;
import org.apache.myfaces.extensions.scripting.cdi.monitor.resources.file.SuffixFileSystemResourceResolver;
import org.apache.myfaces.extensions.scripting.cdi.utils.ClassLoaderUtils;
import org.apache.webbeans.config.WebBeansFinder;
import org.apache.webbeans.container.BeanManagerImpl;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.util.logging.Logger;

/**
 *
 */
public class CdiReloadingListener implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(CdiReloadingListener.class.getName());

    /**
     * The name of the context parameter that specifies the Java source path.
     */
    private static final String SOURCE_PATH_LOCATION = "sourcePathLocation";

    /**
     * The default directory to use as target path for the dynamic compiler.
     */
    private static final String DEFAULT_TARGET_PATH = "/WEB-INF/dynamic-java-classes/";

    // ------------------------------------------ ServletContextListener methods

    @Override
    public void contextInitialized(ServletContextEvent sce) {
         String sourcePathLocation = sce.getServletContext().getInitParameter(SOURCE_PATH_LOCATION);
        if (sourcePathLocation == null) {
            throw new IllegalStateException("You haven't specified the path to your Java source files. "
                    + "Either specify the context parameter '" + SOURCE_PATH_LOCATION
                    + "' or use a different ContextLoaderListener class.");
        }

        File sourcePath = new File(sourcePathLocation);
        if (!sourcePath.exists() || !sourcePath.isDirectory()) {
            throw new IllegalStateException("The source path that you've specified '" + sourcePathLocation
                    + "' doesn't refer to an existing directory, i.e. either it really doesn't exist or it "
                    + "is a file and not a directory.");
        }

        String targetPathLocation = sce.getServletContext().getRealPath(DEFAULT_TARGET_PATH);
        if (targetPathLocation == null) {
            throw new IllegalStateException("Couldn't determine the real path for the compiler target path '"
                    + DEFAULT_TARGET_PATH + "'. Are you sure that you're deploying an exploded web application directory (i.e. no war file)?");
        }

        // As of now I'm using a path within the /WEB-INF/ directory for the compiler target path,
        // which of course could cause problems if you're using archived web applications (.war or
        // .ear, etc.). However, this module doesn't support archived web applications anyway, you
        // have to use an exploded directory. Once this module supports archived web applications
        // as well, we have to think of something else here.
        File targetPath = new File(targetPathLocation);
        if (targetPath.exists()) {
            // In order to ensure that the class loader won't have to deal with precompiled classes
            // (e.g. due to a crash of the server previously, hence the previously dynamically compiled
            // classes haven't been deleted accordingly), we clean up the directory at this point
            // manually.
            deleteDirectory(targetPath);
        } else if (!targetPath.mkdirs()) {
            throw new IllegalStateException("Couldn't create the target path '" + targetPathLocation + "'.");
        }
        targetPath.deleteOnExit();

        ClassLoader parentClassLoader = ClassLoaderUtils.getDefaultClassLoader();
        ReloadingClassLoader reloadingClassLoader = new DependencyAwareReloadingClassLoader(
                new AsmClassReadingDependencyScanner(), parentClassLoader, targetPath);
        reloadingClassLoader.registerReloadingListener(new OpenWebBeansReloadingListener());

        ReloadingBeanManager.install();

        FileMonitoringTask fileMonitoringTask = new FileMonitoringTask();
        fileMonitoringTask.registerResourceMonitor(new SuffixFileSystemResourceResolver(sourcePath, ".java"), new CompilationResourceMonitor(
                reloadingClassLoader, CompilerFactory.createCompiler(), sourcePath, targetPath));
        Thread resourceMonitor = new Thread(fileMonitoringTask);
        resourceMonitor.setDaemon(true);
        resourceMonitor.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

    // ------------------------------------------ Utility methods

    /**
     * <p>Deletes all subdirectories and files within the given directory.</p>
     *
     * @param directory the directory you want to delete
     */
    private static void deleteDirectory(File directory) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                deleteDirectory(file);
            }

            if (!file.delete()) {
                logger.severe("Couldn't delete the file or directory '" + file.getAbsolutePath()
                        + "' while trying to delete the target path directory '" + DEFAULT_TARGET_PATH + "'.");
            }
        }
    }

}
