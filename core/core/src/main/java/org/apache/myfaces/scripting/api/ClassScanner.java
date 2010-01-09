package org.apache.myfaces.scripting.api;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          Generic class scanner interface
 *          which is a helper to plug in external  scanners
 *          as adapters for the annotation and dependency handling
 *          we cannot deal with annotations directly in the core
 *          because we are bound by the jsf 1.2 lower threshold limit
 *          hence this indirection
 */
public interface ClassScanner {

    public void scanPaths();

    @SuppressWarnings("unused")
    public void clearListeners();

    @SuppressWarnings("unused")
    public void addListener(ClassScanListener listener);

    public void addScanPath(String scanPath);

    public void scanClass(Class clazz);

}
