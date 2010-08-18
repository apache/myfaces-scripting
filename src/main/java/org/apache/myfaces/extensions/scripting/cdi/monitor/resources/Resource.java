package org.apache.myfaces.extensions.scripting.cdi.monitor.resources;

import java.io.File;

/**
 * 
 *
 */
public interface Resource {

    /**
     * <p>Returns a reference to this resource on the file system,
     * i.e it returns a reference to a java.io.File object.</p>
     *
     * @return a reference to this resource on the file system
     */
    public File getFile();

    /**
     * <p>Returns the time that the resource denoted by this reference was last modified.</p>
     *
     * @return  A <code>long</code> value representing the time the file was
     *          last modified or <code>0L</code> if the file does not exist
     *          or if an I/O error occurs
     */
    public long lastModified();

}