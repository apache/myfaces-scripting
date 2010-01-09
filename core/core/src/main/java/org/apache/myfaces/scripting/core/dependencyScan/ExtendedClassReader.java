package org.apache.myfaces.scripting.core.dependencyScan;

import org.objectweb.asm.ClassReader;

import java.io.IOException;

/**
 * Class reader for ASM which allows to plug our own loader instead
 * of the default one
 * <p/>
 * (ASM makes too many assumptions regarding the loader)
 */
public class ExtendedClassReader extends ClassReader {
    /**
     * classloader pluggable classreader
     *
     * @param loader    the loader which has to be plugged into the system
     * @param className the class name for the class which has to be investigated
     * @throws IOException in case of a loading error (class cannot be loaded for whatever reason)
     */
    public ExtendedClassReader(ClassLoader loader, String className) throws IOException {
        super(loader.getResourceAsStream(className.replace('.', '/')
                + ".class"));
    }

}
