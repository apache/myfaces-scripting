package org.apache.myfaces.scripting.scratchpad.jci;

import org.apache.commons.jci.compilers.CompilationResult;
import org.apache.commons.jci.compilers.JavaCompiler;
import org.apache.commons.jci.compilers.JavaCompilerFactory;
import org.apache.commons.jci.readers.FileResourceReader;
import org.apache.commons.jci.readers.ResourceReader;
import org.apache.commons.jci.stores.MemoryResourceStore;
import org.apache.commons.jci.stores.ResourceStore;
import org.apache.commons.jci.stores.ResourceStoreClassLoader;

import java.io.File;
import java.net.URL;

/**
 * Loads the classes from our given temp dir
 */
public class TempFileClassLoader extends ClassLoader {

    String[] newClassPath = null;
    JavaCompiler compiler = null;
    boolean nextLevel = false;


    public TempFileClassLoader(ClassLoader classLoader, boolean nextLevel, String... newClassPath) {
        super(classLoader);
        this.newClassPath = newClassPath;
        //lets normalize the paths first
        this.nextLevel = nextLevel;

        for (int cnt = 0; cnt < this.newClassPath.length; cnt++) {
            String path = this.newClassPath[cnt];
            path = path.trim();
            if (path.endsWith("\\") || path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            this.newClassPath[cnt] = path;
        }
        //TODO add a JSR 199 facade for java6
            compiler = new JavaCompilerFactory().createCompiler("eclipse");
    }

    @Override
    public URL getResource(String s) {
        return super.getResource(s);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public Class loadClass(String s) throws ClassNotFoundException {
        String finalName = s.replaceAll("\\.", File.separator);
        finalName = finalName + ".java";

        String[] toCompile = new String[1];
        toCompile[0] = finalName;

        ResourceReader reader = new FileResourceReader(new File(this.newClassPath[0]));
        System.out.println(reader.isAvailable(toCompile[0]));
        //we load the class as expected
        MemoryResourceStore target = new MemoryResourceStore();
        CompilationResult result = null;

       // if(nextLevel) {
      //     result = compiler.compile(toCompile, reader, target, new TempFileClassLoader(getParent(), false, newClassPath));
      //  } else {
           result = compiler.compile(toCompile, reader, target);
      //  }



        if (result.getErrors().length == 0) {
            ResourceStore[] stores = {target};
            ResourceStoreClassLoader loader = new ResourceStoreClassLoader(getParent(), stores);
            return loader.loadClass(s);
        } else {
            System.out.println(result.getErrors().length + " errors");
            System.out.println(result.getWarnings().length + " warnings");
        }


        return super.loadClass(s);
    }
}
