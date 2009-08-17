/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.myfaces.scripting.jsf.dynamicdecorators.implemetations;

import org.apache.myfaces.scripting.core.util.DynamicClassIdentifier;
import org.apache.myfaces.scripting.api.Decorated;
import org.apache.myfaces.scripting.core.util.ProxyUtils;
import org.apache.myfaces.scripting.jsf.ScriptingConst;

import javax.el.*;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.NavigationHandler;
import javax.faces.application.StateManager;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.el.*;
import javax.faces.event.ActionListener;
import javax.faces.validator.Validator;
import javax.servlet.ServletRequest;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * our decorating applicstion
 * which should resolve our bean issues within a central
 * bean processing interceptor
 *
 * @author Werner Punz
 */
public class ApplicationProxy extends Application implements Decorated {

    Application _delegate = null;

    private boolean alreadyWovenInRequest(String clazz) {
        //todo also enable portlets here
        ServletRequest req = (ServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        if (req.getAttribute(ScriptingConst.SCRIPTING_REQUSINGLETON + clazz) == null) {
            req.setAttribute(ScriptingConst.SCRIPTING_REQUSINGLETON + clazz, "");
            return false;
        }
        return false;
    }

    //TODO add a proxy for the el resolvers as well

    public void addELResolver(ELResolver elResolver) {
        weaveDelegate();
        if (!(elResolver instanceof ELResolverProxy))
            elResolver = new ELResolverProxy(elResolver);
        _delegate.addELResolver(elResolver);
    }

    private void weaveDelegate() {
        if (_delegate != null) {
            _delegate = (Application) ProxyUtils.getWeaver().reloadScriptingInstance(_delegate);
        }    
    }

    public ELResolver getELResolver() {
        weaveDelegate();
        ELResolver retVal = _delegate.getELResolver();
        if (!(retVal instanceof ELResolverProxy))
            retVal = new ELResolverProxy(retVal);
        return retVal;

    }

    //TOD add a weaving for resource bundles
    public ResourceBundle getResourceBundle(FacesContext facesContext, String s) throws FacesException, NullPointerException {
        weaveDelegate();
        return _delegate.getResourceBundle(facesContext, s);
    }

    public UIComponent createComponent(ValueExpression valueExpression, FacesContext facesContext, String s) throws FacesException, NullPointerException {
        weaveDelegate();
        System.out.println("create1");
        UIComponent component = _delegate.createComponent(valueExpression, facesContext, s);

        /*we are reweaving on the fly because we cannot be sure if
         * the class is not recycled all the time in the creation
         * code, in the renderer we do it on method base
         * due to the fact that our renderers are recycled via
         * a flyweight pattern
         *
         *
         * Also we cannot proxy here because there is no UIComponent interface
         * maybe in the long run we can make a decorator here instead
         * but for now lets try it this way
         */
        if (DynamicClassIdentifier.isDynamic(component.getClass()) && !alreadyWovenInRequest(component.toString())) {
            /*once it was tainted we have to recreate all the time*/
            component = (UIComponent) ProxyUtils.getWeaver().reloadScriptingInstance(component);
            alreadyWovenInRequest(component.toString());
        }
        return component;

    }

    public ExpressionFactory getExpressionFactory() {
        weaveDelegate();
        return _delegate.getExpressionFactory();
    }

    public void addELContextListener(ELContextListener elContextListener) {
        weaveDelegate();
        if (DynamicClassIdentifier.isDynamic(elContextListener.getClass()))
            elContextListener = (ELContextListener) ProxyUtils.createMethodReloadingProxyFromObject(elContextListener, ELContextListener.class);
        _delegate.addELContextListener(elContextListener);
    }

    public void removeELContextListener(ELContextListener elContextListener) {
        //TODO which el context listener is coming in our normal one
        //or the reloaded one?
        weaveDelegate();
        _delegate.removeELContextListener(elContextListener);
    }

    public ELContextListener[] getELContextListeners() {
        weaveDelegate();
        return _delegate.getELContextListeners();
    }

    public Object evaluateExpressionGet(FacesContext facesContext, String s, Class aClass) throws ELException {
        weaveDelegate();
        //good place for a dynamic reloading check as well
        Object retVal = _delegate.evaluateExpressionGet(facesContext, s, aClass);
        if (DynamicClassIdentifier.isDynamic(retVal.getClass()))
            retVal = ProxyUtils.getWeaver().reloadScriptingInstance(retVal);
        return retVal;
    }

    public ActionListener getActionListener() {
        weaveDelegate();
        ActionListener retVal = _delegate.getActionListener();
        if (DynamicClassIdentifier.isDynamic(retVal.getClass()))
            retVal = (ActionListener) ProxyUtils.createMethodReloadingProxyFromObject(retVal, ActionListener.class);
        return retVal;
    }

    public void setActionListener(ActionListener actionListener) {
        weaveDelegate();
        if (DynamicClassIdentifier.isDynamic(actionListener.getClass()))
            actionListener = (ActionListener) ProxyUtils.createMethodReloadingProxyFromObject(actionListener, ActionListener.class);
        _delegate.setActionListener(actionListener);
    }

    public Locale getDefaultLocale() {
        weaveDelegate();
        return _delegate.getDefaultLocale();
    }

    public void setDefaultLocale(Locale locale) {
        weaveDelegate();
        _delegate.setDefaultLocale(locale);
    }

    public String getDefaultRenderKitId() {
        weaveDelegate();
        return _delegate.getDefaultRenderKitId();
    }

    public void setDefaultRenderKitId(String s) {
        weaveDelegate();
        _delegate.setDefaultRenderKitId(s);
    }

    public String getMessageBundle() {
        weaveDelegate();
        return _delegate.getMessageBundle();
    }

    public void setMessageBundle(String s) {
        weaveDelegate();
        _delegate.setMessageBundle(s);
    }

    public NavigationHandler getNavigationHandler() {
        weaveDelegate();
        //defined in the setter to speed things up a little
        NavigationHandler retVal = _delegate.getNavigationHandler();
        if (retVal != null && DynamicClassIdentifier.isDynamic(retVal.getClass()))
            retVal = new NavigationHandlerProxy(retVal);
        return retVal;
    }

    public void setNavigationHandler(NavigationHandler navigationHandler) {
        weaveDelegate();
        if (navigationHandler != null && DynamicClassIdentifier.isDynamic(navigationHandler.getClass()))
            navigationHandler = new NavigationHandlerProxy(navigationHandler);
        _delegate.setNavigationHandler(navigationHandler);
    }

    public PropertyResolver getPropertyResolver() {
        weaveDelegate();
        return _delegate.getPropertyResolver();
    }

    public void setPropertyResolver(PropertyResolver propertyResolver) {
        weaveDelegate();
        _delegate.setPropertyResolver(propertyResolver);
    }

    public VariableResolver getVariableResolver() {
        weaveDelegate();
        VariableResolver variableResolver = _delegate.getVariableResolver();
        if (!(variableResolver instanceof VariableResolverProxy))
            variableResolver = new VariableResolverProxy(variableResolver);
        return variableResolver;
    }

    public void setVariableResolver(VariableResolver variableResolver) {
        weaveDelegate();
        if (!(variableResolver instanceof VariableResolverProxy))
            variableResolver = new VariableResolverProxy(variableResolver);

        _delegate.setVariableResolver(variableResolver);
    }

    public ViewHandler getViewHandler() {
        weaveDelegate();
        ViewHandler handler = _delegate.getViewHandler();

        /*
        We proxy here to emable dynamic reloading for
        methods in the long run, as soon as we hit
        java all our groovy reloading code is lost
        hence we have to work with proxies here
        */
        if (DynamicClassIdentifier.isDynamic(handler.getClass()))
            handler = (ViewHandlerProxy) new ViewHandlerProxy(handler);
        return handler;
    }

    public void setViewHandler(ViewHandler viewHandler) {
        weaveDelegate();
        /*make sure you have the delegates as well in properties*/
        if (DynamicClassIdentifier.isDynamic(viewHandler.getClass()))
            viewHandler = (ViewHandlerProxy) new ViewHandlerProxy(viewHandler);

        _delegate.setViewHandler(viewHandler);
    }

    public StateManager getStateManager() {
        weaveDelegate();
        return _delegate.getStateManager();
    }

    public void setStateManager(StateManager stateManager) {
        weaveDelegate();
        _delegate.setStateManager(stateManager);
    }

    public void addComponent(String s, String s1) {
        weaveDelegate();
        _delegate.addComponent(s, s1);
    }

    public UIComponent createComponent(String s) throws FacesException {
        weaveDelegate();
        //the components are generated anew very often
        //we cannot do an on object weaving here
        UIComponent component = _delegate.createComponent(s);

        /*we are reweaving on the fly because we cannot be sure if
        * the class is not recycled all the time in the creation
        * code, in the renderer we do it on method base
        * due to the fact that our renderers are recycled via
        * a flyweight pattern*/
        if (DynamicClassIdentifier.isDynamic(component.getClass()) && !alreadyWovenInRequest(component.toString())) {
            /*once it was tainted we have to recreate all the time*/
            component = (UIComponent) ProxyUtils.getWeaver().reloadScriptingInstance(component);
            alreadyWovenInRequest(component.toString());
        }
        return component;
    }

    public UIComponent createComponent(ValueBinding valueBinding, FacesContext facesContext, String s) throws FacesException {
        weaveDelegate();
        UIComponent component = _delegate.createComponent(valueBinding, facesContext, s);

        /*we are reweaving on the fly because we cannot be sure if
     * the class is not recycled all the time in the creation
     * code, in the renderer we do it on method base
     * due to the fact that our renderers are recycled via
     * a flyweight pattern*/
        if (DynamicClassIdentifier.isDynamic(component.getClass()) && !alreadyWovenInRequest(component.toString())) {
            /*once it was tainted we have to recreate all the time*/
            component = (UIComponent) ProxyUtils.getWeaver().reloadScriptingInstance(component);
            alreadyWovenInRequest(component.toString());
        }
        return component;
    }

    public Iterator<String> getComponentTypes() {
        weaveDelegate();
        return _delegate.getComponentTypes();
    }

    public void addConverter(String s, String s1) {
        weaveDelegate();
        _delegate.addConverter(s, s1);
    }

    public void addConverter(Class aClass, String s) {
        weaveDelegate();
        _delegate.addConverter(aClass, s);
    }

    public Converter createConverter(String s) {
        weaveDelegate();
        Converter retVal = _delegate.createConverter(s);
        /**
         * since createConverter is called only once
         * we have to work with method reloading proxies
         * we cannot use this technique extensively for speed reasons
         * most of the time it is fine just to work with
         *
         * reloading objects at their interception points
         */
        if (DynamicClassIdentifier.isDynamic(retVal.getClass())) {
            retVal = (Converter) ProxyUtils.createMethodReloadingProxyFromObject(retVal, Converter.class);

        }

        return retVal;
    }

    public Converter createConverter(Class aClass) {
        weaveDelegate();
        Converter retVal = _delegate.createConverter(aClass);
        if (retVal != null && DynamicClassIdentifier.isDynamic(retVal.getClass())) {
            retVal = (Converter) ProxyUtils.createMethodReloadingProxyFromObject(retVal, Converter.class);
        }

        return retVal;
    }

    public Iterator<String> getConverterIds() {
        weaveDelegate();
        return _delegate.getConverterIds();
    }

    public Iterator<Class> getConverterTypes() {
        weaveDelegate();
        return _delegate.getConverterTypes();
    }

    public MethodBinding createMethodBinding(String s, Class[] classes) throws ReferenceSyntaxException {
        weaveDelegate();
        return _delegate.createMethodBinding(s, classes);
    }

    public Iterator<Locale> getSupportedLocales() {
        weaveDelegate();
        return _delegate.getSupportedLocales();
    }

    public void setSupportedLocales(Collection<Locale> locales) {
        weaveDelegate();
        _delegate.setSupportedLocales(locales);
    }

    public void addValidator(String s, String s1) {
        weaveDelegate();
        _delegate.addValidator(s, s1);
    }

    public Validator createValidator(String s) throws FacesException {
        weaveDelegate();
        Validator retVal = _delegate.createValidator(s);
        if (DynamicClassIdentifier.isDynamic(retVal.getClass()) && !Proxy.isProxyClass(retVal.getClass())) {
            retVal = (Validator) ProxyUtils.createMethodReloadingProxyFromObject(retVal, Validator.class);
        }
        return retVal;
    }

    public Iterator<String> getValidatorIds() {
        weaveDelegate();
        return _delegate.getValidatorIds();
    }

    public ValueBinding createValueBinding(String s) throws ReferenceSyntaxException {
        weaveDelegate();
        return _delegate.createValueBinding(s);
    }

    public ApplicationProxy(Application delegate) {
        _delegate = delegate;
    }


    public Object getDelegate() {
        return _delegate;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
