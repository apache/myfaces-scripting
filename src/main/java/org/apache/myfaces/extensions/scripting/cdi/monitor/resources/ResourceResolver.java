package org.apache.myfaces.extensions.scripting.cdi.monitor.resources;

/**
 * 
 *
 */
public interface ResourceResolver {

    /**
     * <p></p>
     *
     * @param resourceHandler the callback handler to use for handling found resources 
     */
    public void resolveResources(ResourceCallback resourceHandler);

    /**
     * <p>A callback interface that you can use to handle the resources
     * found by a resource resolver.</p>
     */
    public interface ResourceCallback {

        /**
         * <p>Callback method that will be invoked by a resource resolver
         * once it finds another resource that should be handled.</p>
         * 
         * @param resource the resource encountered by the resource resolver
         */
        public boolean handle(Resource resource);

    }

}