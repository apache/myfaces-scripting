package org.apache.myfaces.scripting.loaders.java.jsr199;

import org.apache.myfaces.scripting.api.DynamicCompiler;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import javax.tools.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Locale;

/**
 * A compiler facade encapsulating the JSR 199
 * so that we can switch the implementations
 * of connecting to javac on the fly
 */
public class CompilerFacade implements DynamicCompiler {

    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector();
    StandardJavaFileManager fileManager = null;
    private static File tempDir = null;
    private static final String FILE_SEPARATOR = File.separator;

    public CompilerFacade() {
        super();
        fileManager = compiler.getStandardFileManager(diagnosticCollector, null, null);

        if (tempDir == null) {
            synchronized (this.getClass()) {
                if (tempDir != null) {
                    return;
                }
                String baseTempPath = System.getProperty("java.io.tmpdir");
                String tempDirName = "myfaces_compilation_" + Math.random();

                tempDir = new File(baseTempPath + FILE_SEPARATOR + tempDirName);
                while (tempDir.exists()) {
                    tempDirName = "myfaces_compilation_" + System.currentTimeMillis() + Math.random();
                    tempDir = new File(baseTempPath + FILE_SEPARATOR + tempDirName);
                }
                tempDir.mkdirs();
                tempDir.deleteOnExit();
            }
        }
    }


    class RecompiledJavaClassloader extends ClassLoader {
        RecompiledJavaClassloader(ClassLoader classLoader) {
            super(classLoader);
        }

        RecompiledJavaClassloader() {
        }

        @Override
        public Class<?> loadClass(String className) throws ClassNotFoundException {
            //check if our class exists in the tempDir
            String classFile = className.replaceAll("\\.", FILE_SEPARATOR) + ".class";
            File target = new File(tempDir.getAbsolutePath() + FILE_SEPARATOR + classFile);
            if (target.exists()) {

                FileInputStream iStream = null;
                int fileLength = (int) target.length();
                byte[] fileContent = new byte[fileLength];

                try {
                    iStream = new FileInputStream(target);
                    iStream.read(fileContent);
                    // Erzeugt aus dem byte Feld ein Class Object.
                    return super.defineClass(className, fileContent, 0, fileLength);

                } catch (Exception e) {
                    throw new ClassNotFoundException(e.toString());
                } finally {
                    if (iStream != null) {
                        try {
                            iStream.close();
                        } catch (Exception e) {
                        }
                    }
                }
            }

            return super.loadClass(className);    //To change body of overridden methods use File | Settings | File Templates.
        }
    }


    public Class compileFile(String sourceRoot, String classPath, String filePath) throws ClassNotFoundException {
        Iterable<? extends JavaFileObject> fileObjects = fileManager.getJavaFileObjects(sourceRoot + FILE_SEPARATOR + filePath);

        //TODO add the core jar from our lib dir
        //the compiler otherwise cannot find the file
        String[] options = new String[]{"-cp",classPath,"-d", tempDir.getAbsolutePath(), "-sourcepath", sourceRoot, "-g"};
        compiler.getTask(null, fileManager, diagnosticCollector, Arrays.asList(options), null, fileObjects).call();
        //TODO collect the diagnostics and if an error was issued dump it on the log
        //and throw an unmanaged exeption which routes later on into myfaces
        if(diagnosticCollector.getDiagnostics().size() > 0) {
            Log log = LogFactory.getLog(this.getClass());
            StringBuilder errors = new StringBuilder();
            for (Diagnostic diagnostic : diagnosticCollector.getDiagnostics())  {
                String error = "Error on line" +
                                  diagnostic.getMessage(Locale.getDefault())+"------"+
                                  diagnostic.getLineNumber()+" File:"+
                                  diagnostic.getSource().toString();
                log.error(error);
                errors.append(error);

            }
            throw new ClassNotFoundException("Compile error of java file:"+errors.toString());
        }

        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        if (!(oldClassLoader instanceof RecompiledJavaClassloader)) {
            try {
                RecompiledJavaClassloader classLoader = new RecompiledJavaClassloader(oldClassLoader);
                Thread.currentThread().setContextClassLoader(classLoader);
                String classFile = filePath.replaceAll("\\\\", ".").replaceAll("\\/", ".");
                classFile = classFile.substring(0, classFile.lastIndexOf("."));

                return classLoader.loadClass(classFile);
            } finally {
                Thread.currentThread().setContextClassLoader(oldClassLoader);
            }
        }
        return null;
    }
}