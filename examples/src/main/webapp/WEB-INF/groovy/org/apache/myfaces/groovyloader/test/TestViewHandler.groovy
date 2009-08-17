package org.apache.myfaces.groovyloader.test

import javax.faces.application.ViewHandler
import javax.faces.context.FacesContext
import javax.faces.component.UIViewRoot

/**
 * Delegating View handler
 * you have to expose the internal properties
 * to enable on method reloading
 * (if it is possible at all)
 *
 */
class TestViewHandler extends ViewHandler {
    //setters and getters are added implicitely
    ViewHandler delegate;

    /**
     * needed for reloading
     */
    public TestViewHandler() {
        super();
    }

    public TestViewHandler(ViewHandler delegate) {
        super();
        this.delegate = delegate;
    }


    public Locale calculateLocale(FacesContext facesContext) {
        return delegate.calculateLocale(facesContext);
    }

    public String calculateRenderKitId(FacesContext facesContext) {
        return delegate.calculateRenderKitId(facesContext);
    }

    public UIViewRoot createView(FacesContext facesContext, String s) {
        return delegate.createView(facesContext, s);
    }

    public String getActionURL(FacesContext facesContext, String s) {
        return delegate.getActionURL(facesContext, s);
    }

    public String getResourceURL(FacesContext facesContext, String s) {
        return delegate.getResourceURL(facesContext, s);
    }

    public void renderView(FacesContext facesContext, UIViewRoot uiViewRoot) {
        println "hello world from our view handler2 RENDERVIEW"

        delegate.renderView(facesContext, uiViewRoot);
    }

    public UIViewRoot restoreView(FacesContext facesContext, String s) {

        return delegate.restoreView(facesContext, s);
    }

    public void writeState(FacesContext facesContext) {
        delegate.writeState(facesContext);
    }


}