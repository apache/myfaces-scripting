package org.apache.myfaces.scripting.core.dependencyScan;

import org.objectweb.asm.ClassReader;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: werpu2
 * Date: 23.12.2009
 * Time: 09:01:10
 * To change this template use File | Settings | File Templates.
 */
public class ExtendedClassReader extends ClassReader {
    /**
     * classloader pluggable classreader
     *
     * @param loader
     * @param className
     * @throws IOException
     */
    public ExtendedClassReader(ClassLoader loader, String className) throws IOException {
         super(loader.getResourceAsStream(className.replace('.', '/')
                + ".class"));
    }

}
