package org.apache.myfaces.scripting.loaders.java;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.scripting.core.util.ClassUtils;
import org.apache.myfaces.scripting.core.util.FileUtils;
import org.apache.myfaces.scripting.core.util.WeavingContext;
import org.apache.myfaces.scripting.refresh.RefreshContext;
import org.apache.myfaces.scripting.refresh.ReloadingMetadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A specialized non tainting classloader for our scanners
 */
public class ScannerClassloader extends ClassLoader {

    File tempDir = null;

    Map<String, Class> _alreadyScanned = new HashMap<String, Class>();

    public ScannerClassloader(ClassLoader classLoader, int scriptingEngine, String engineExtension, File tempDir) {
        super(classLoader);

        this.tempDir = tempDir;
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        File resource = new File(tempDir.getAbsolutePath() + File.separator + name);
        if (resource.exists()) {
            try {
                return new FileInputStream(resource);
            } catch (FileNotFoundException e) {
                return super.getResourceAsStream(name);
            }
        }
        return super.getResourceAsStream(name);
    }

    public File getClassFile(String className) {
        return ClassUtils.classNameToFile(tempDir.getAbsolutePath(), className);
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        //check if our class exists in the tempDir

        if (_alreadyScanned.containsKey(className)) {
            return _alreadyScanned.get(className);
        }

        File target = getClassFile(className);
        if (!target.exists()) {
            return super.loadClass(className);
        }

        ReloadingMetadata data = WeavingContext.getFileChangedDaemon().getClassMap().get(className);
        if (data != null && !data.isTainted()) {
            return data.getAClass();
        }

        FileInputStream iStream = null;

        int fileLength = -1;
        byte[] fileContent = null;
        try {
            //we cannot load while a compile is in progress
            //we have to wait until it is one
            synchronized (RefreshContext.COMPILE_SYNC_MONITOR) {
                fileLength = (int) target.length();
                fileContent = new byte[fileLength];
                iStream = new FileInputStream(target);
                iStream.read(fileContent);
            }
            // Erzeugt aus dem byte Feld ein Class Object.
            Class retVal = null;

            //we have to do it here because just in case
            //a dependend class is loaded as well we run into classcast exceptions

            //storeReloadableDefinitions(className, target, fileLength, fileContent)
            retVal = super.defineClass(className, fileContent, 0, fileLength);
            _alreadyScanned.put(className, retVal);
            return retVal;
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

}

