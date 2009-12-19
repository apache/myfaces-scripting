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

import org.apache.myfaces.scripting.api.Decorated;
import org.apache.myfaces.scripting.api.ScriptingConst;
import org.apache.myfaces.scripting.core.util.WeavingContext;

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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * our decorating applicstion
 * which should resolve our bean issues within a central
 * bean processing interceptor
 *
 *
 * component
 *
 *   komponente A Šndert sich
 *   Renderer Šndert sich nicht
 *
 *   class Renderer {
 *          public method (UIComponent myComp) {
 *                  ((MyComponent)myComp)
 }
 }
 *
 *
 *
 * @author Werner Punz
 */
public class ApplicationProxy extends Application implements Decorated {

    volatile Application _delegate;



    public ApplicationProxy(Application delegate) {
        _delegate = delegate;
    }

    //TODO add a proxy for the el resolvers as well

    public void addELResolver(ELResolver elResolver) {
        weaveDelegate();
        //TODO this can be problematic if several libraries add their own proxies
        // that way then might get get a cyclic stack
        //under normal circumstances this should not happen
        //because addElResolver is called once and getElResolver
        //does not change the stack afterwards in the worst case
        //we might get 2 of our proxies in the delegate stack

        //the same goes for the rest of the factory stuff
        if (!(elResolver instanceof ELResolverProxy))
            elResolver = new ELResolverProxy(elResolver);
        _delegate.addELResolver(elResolver);
    }

    private void weaveDelegate() {
        if (_delegate != null) {
            _delegate = (Application) WeavingContext.getWeaver().reloadScriptingInstance(_delegate, ScriptingConst.ARTEFACT_TYPE_APPLICATION);
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
        if (WeavingContext.isDynamic(component.getClass()) && !alreadyWovenInRequest(component.toString())) {
            /*once it was tainted we have to recreate all the time*/
            component = (UIComponent) WeavingContext.getWeaver().reloadScriptingInstance(component, ScriptingConst.ARTEFACT_TYPE_COMPONENT);
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
        if (WeavingContext.isDynamic(elContextListener.getClass()))
            elContextListener = (ELContextListener) WeavingContext.createMethodReloadingProxyFromObject(elContextListener, ELContextListener.class, ScriptingConst.ARTEFACT_TYPE_ELCONTEXTLISTENER);
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
        if (WeavingContext.isDynamic(retVal.getClass()))
            retVal = WeavingContext.getWeaver().reloadScriptingInstance(retVal, ScriptingConst.ARTEFACT_TYPE_MANAGEDBEAN);
        return retVal;
    }

    public ActionListener getActionListener() {
        weaveDelegate();
        ActionListener retVal = _delegate.getActionListener();
        if (WeavingContext.isDynamic(retVal.getClass()))
            retVal = (ActionListener) WeavingContext.createMethodReloadingProxyFromObject(retVal, ActionListener.class, ScriptingConst.ARTEFACT_TYPE_ACTIONLISTENER);
        return retVal;
    }

    public void setActionListener(ActionListener actionListener) {
        weaveDelegate();
        if (WeavingContext.isDynamic(actionListener.getClass()))
            actionListener = (ActionListener) WeavingContext.createMethodReloadingProxyFromObject(actionListener, ActionListener.class, ScriptingConst.ARTEFACT_TYPE_ACTIONLISTENER);
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
        if (retVal != null && WeavingContext.isDynamic(retVal.getClass()))
            retVal = new NavigationHandlerProxy(retVal);
        return retVal;
    }

    public void setNavigationHandler(NavigationHandler navigationHandler) {
        weaveDelegate();
        if (navigationHandler != null && WeavingContext.isDynamic(navigationHandler.getClass()))
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
        if (WeavingContext.isDynamic(handler.getClass()))
            handler = (ViewHandlerProxy) new ViewHandlerProxy(handler);
        return handler;
    }

    public void setViewHandler(ViewHandler viewHandler) {
        weaveDelegate();
        /*make sure you have the delegates as well in properties*/
        if (WeavingContext.isDynamic(viewHandler.getClass()))
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

    public void addComponent(String componentType, String componentClass) {
        weaveDelegate();
        _delegate.addComponent(componentType, componentClass);
        //TODO handle this properly
       // WeavingContext.getRefreshContext().getComponentRendererDependencies(componentClass, "");
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
        return (UIComponent) reloadInstance(component, ScriptingConst.ARTEFACT_TYPE_COMPONENT);
    }

    public UIComponent createComponent(ValueBinding valueBinding, FacesContext facesContext, String s) throws FacesException {
        weaveDelegate();
        UIComponent component = _delegate.createComponent(valueBinding, facesContext, s);

        /*we are reweaving on the fly because we cannot be sure if
         * the class is not recycled all the time in the creation
         * code, in the renderer we do it on method base
         * due to the fact that our renderers are recycled via
         * a flyweight pattern*/
        return (UIComponent) reloadInstance(component, ScriptingConst.ARTEFACT_TYPE_COMPONENT);
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
        if (WeavingContext.isDynamic(retVal.getClass())) {
            retVal = (Converter) WeavingContext.createMethodReloadingProxyFromObject(retVal, Converter.class, ScriptingConst.ARTEFACT_TYPE_CONVERTER);

        }

        return retVal;
    }

    public Converter createConverter(Class aClass) {
        weaveDelegate();
        Converter retVal = _delegate.createConverter(aClass);
        if (retVal != null && WeavingContext.isDynamic(retVal.getClass())) {
            retVal = (Converter) WeavingContext.createMethodReloadingProxyFromObject(retVal, Converter.class, ScriptingConst.ARTEFACT_TYPE_CONVERTER);
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
        if (WeavingContext.isDynamic(retVal.getClass()) && !Proxy.isProxyClass(retVal.getClass())) {
            //todo bypass the serialisation problem on validators
            retVal = (Validator) reloadInstance(retVal, ScriptingConst.ARTEFACT_TYPE_VALIDATOR); //WeavingContext.createMethodReloadingProxyFromObject(retVal, Validator.class, ScriptingConst.ARTEFACT_TYPE_VALIDATOR);
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


    public Object getDelegate() {
        return _delegate;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private final Object reloadInstance(Object instance, int artefactType) {
        if (instance == null) {
            return null;
        }
        if (WeavingContext.isDynamic(instance.getClass()) && !alreadyWovenInRequest(instance.toString())) {
            instance = WeavingContext.getWeaver().reloadScriptingInstance(instance, artefactType);
            alreadyWovenInRequest(instance.toString());
        }
        return instance;
    }


    private final boolean alreadyWovenInRequest(String clazz) {
        //portlets now can be enabled thanks to the jsf2 indirections regarding the external context
        Map reqMap = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
        if (reqMap.get(ScriptingConst.SCRIPTING_REQUSINGLETON + clazz) == null) {
            reqMap.put(ScriptingConst.SCRIPTING_REQUSINGLETON + clazz, "");
            return false;
        }
        return true;
    }

}
