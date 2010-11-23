package org.apache.myfaces.groovyloader.test

import javax.faces.application.ResourceHandler

import javax.faces.context.FacesContext
import javax.faces.application.Resource
/**
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public class TestResourceHandler extends ResourceHandler {

    ResourceHandler _delegate

    public TestResourceHandler(delegate) {
        _delegate = delegate;
    }

    public TestResourceHandler() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public Resource createResource(String resourceName) {
        return _delegate.createResource(resourceName)
    }

    public Resource createResource(String resourceName, String libraryName) {
        return _delegate.createResource(resourceName, libraryName)
    }

    public Resource createResource(String resourceName, String libraryName, String contentType) {
        return _delegate.createResource(resourceName, libraryName, contentType)
    }

    public String getRendererTypeForResourceName(String resourceName) {
        return _delegate.getRendererTypeForResourceName(resourceName)
    }

    public void handleResourceRequest(FacesContext context) throws IOException {
        System.out.println("cggsfdlkghfsdkjlghkjfgds")
        _delegate.handleResourceRequest(context)
    }

    public boolean isResourceRequest(FacesContext context) {
        return _delegate.isResourceRequest(context)
    }

    public boolean libraryExists(String libraryName) {
        return _delegate.libraryExists(libraryName)

    }

    public ResourceHandler getDelegate() {
        return _delegate
    }

    public void setDelegate(ResourceHandler delegate) {
        _delegate = delegate
    }
}