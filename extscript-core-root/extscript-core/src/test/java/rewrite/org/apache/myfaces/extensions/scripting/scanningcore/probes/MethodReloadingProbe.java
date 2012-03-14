package org.apache.myfaces.extensions.scripting.scanningcore.probes;

/**
 * Interface which will allow the proxying of our probe
 * in reloading handlers
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public interface MethodReloadingProbe {

    /**
     * testmethod 1 goes through
     */
    public void testMethod1();

    /**
     * this one throws an exception
     */
    public void testMethod2();
}
