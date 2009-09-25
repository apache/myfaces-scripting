package org.apache.myfaces.scripting.api;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          Generic annotation scanner interface
 *          which is a helper to plug in external annotation scanners
 *          as adapters for the annotation handling
 *          we cannot deal with annotations directly in the core
 *          because we are bound by the jsf 1.2 lower threshold limit
 *          hence this indirection
 */
public interface AnnotationScanner {

    public void scanPaths();

    public void clearListeners();

    public void addListener(AnnotationScanListener listener);

    public void addScanPath(String scanPath);

    public void scanClass(Class clazz);

}
