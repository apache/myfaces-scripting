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
package org.apache.myfaces.javaloader.componentTest;

import org.apache.myfaces.javaloader.other.Markable;

import javax.faces.component.UIInput;
import javax.faces.component.FacesComponent;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *
 * adding a prop and we get following error
 *
 * javax.faces.view.facelets.TagAttributeException: /helloWorld.xhtmlat line 39 and column 69 testAttr2="zzz" object is not an instance of declaring class
	at org.apache.myfaces.view.facelets.tag.BeanPropertyTagRule$LiteralPropertyMetadata.applyMetadata(BeanPropertyTagRule.java:92)
	at org.apache.myfaces.view.facelets.tag.MetadataImpl.applyMetadata(MetadataImpl.java:45)
	at javax.faces.view.facelets.MetaTagHandler.setAttributes(MetaTagHandler.java:68)
	at javax.faces.view.facelets.DelegatingMetaTagHandler.setAttributes(DelegatingMetaTagHandler.java:93)
	at org.apache.myfaces.view.facelets.tag.jsf.ComponentTagHandlerDelegate.apply(ComponentTagHandlerDelegate.java:153)
	at javax.faces.view.facelets.DelegatingMetaTagHandler.apply(DelegatingMetaTagHandler.java:54)
	at javax.faces.view.facelets.CompositeFaceletHandler.apply(CompositeFaceletHandler.java:51)
	at javax.faces.view.facelets.DelegatingMetaTagHandler.applyNextHandler(DelegatingMetaTagHandler.java:59)
	at org.apache.myfaces.view.facelets.tag.jsf.ComponentTagHandlerDelegate.apply(ComponentTagHandlerDelegate.java:200)
	at javax.faces.view.facelets.DelegatingMetaTagHandler.apply(DelegatingMetaTagHandler.java:54)
	at javax.faces.view.facelets.CompositeFaceletHandler.apply(CompositeFaceletHandler.java:51)
	at javax.faces.view.facelets.DelegatingMetaTagHandler.applyNextHandler(DelegatingMetaTagHandler.java:59)
	at org.apache.myfaces.view.facelets.tag.jsf.ComponentTagHandlerDelegate.apply(ComponentTagHandlerDelegate.java:200)
	at javax.faces.view.facelets.DelegatingMetaTagHandler.apply(DelegatingMetaTagHandler.java:54)
	at org.apache.myfaces.view.facelets.tag.ui.DefineHandler.applyDefinition(DefineHandler.java:86)
	at org.apache.myfaces.view.facelets.tag.ui.CompositionHandler.apply(CompositionHandler.java:167)
	at org.apache.myfaces.view.facelets.impl.DefaultFaceletContext$TemplateManager.apply(DefaultFaceletContext.java:419)
	at org.apache.myfaces.view.facelets.impl.DefaultFaceletContext.includeDefinition(DefaultFaceletContext.java:382)
	at org.apache.myfaces.view.facelets.tag.ui.InsertHandler.apply(InsertHandler.java:93)
	at javax.faces.view.facelets.CompositeFaceletHandler.apply(CompositeFaceletHandler.java:51)
	at org.apache.myfaces.view.facelets.compiler.NamespaceHandler.apply(NamespaceHandler.java:57)
	at javax.faces.view.facelets.CompositeFaceletHandler.apply(CompositeFaceletHandler.java:51)
	at org.apache.myfaces.view.facelets.compiler.EncodingHandler.apply(EncodingHandler.java:45)
	at org.apache.myfaces.view.facelets.impl.DefaultFacelet.include(DefaultFacelet.java:268)
	at org.apache.myfaces.view.facelets.impl.DefaultFacelet.include(DefaultFacelet.java:315)
	at org.apache.myfaces.view.facelets.impl.DefaultFacelet.include(DefaultFacelet.java:293)
	at org.apache.myfaces.view.facelets.impl.DefaultFaceletContext.includeFacelet(DefaultFaceletContext.java:178)
	at org.apache.myfaces.view.facelets.tag.ui.CompositionHandler.apply(CompositionHandler.java:140)
	at org.apache.myfaces.view.facelets.compiler.NamespaceHandler.apply(NamespaceHandler.java:57)
	at org.apache.myfaces.view.facelets.compiler.EncodingHandler.apply(EncodingHandler.java:45)
	at org.apache.myfaces.view.facelets.impl.DefaultFacelet.apply(DefaultFacelet.java:103)
	at org.apache.myfaces.view.facelets.FaceletViewDeclarationLanguage.buildView(FaceletViewDeclarationLanguage.java:255)
	at org.apache.myfaces.lifecycle.RenderResponseExecutor.execute(RenderResponseExecutor.java:54)
	at org.apache.myfaces.lifecycle.LifecycleImpl.render(LifecycleImpl.java:201)
	at org.apache.myfaces.scripting.jsf.dynamicdecorators.implemetations.LifefcycleProxy.render(LifefcycleProxy.java:75)
	at javax.faces.webapp.FacesServlet.service(FacesServlet.java:191)
	at org.mortbay.jetty.servlet.ServletHolder.handle(ServletHolder.java:491)
	at org.mortbay.jetty.servlet.ServletHandler$CachedChain.doFilter(ServletHandler.java:1074)
	at org.apache.myfaces.scripting.servlet.ScriptingServletFilter.doFilter(ScriptingServletFilter.java:55)
	at org.mortbay.jetty.servlet.ServletHandler$CachedChain.doFilter(ServletHandler.java:1065)
	at org.mortbay.jetty.servlet.ServletHandler.handle(ServletHandler.java:365)
	at org.mortbay.jetty.security.SecurityHandler.handle(SecurityHandler.java:185)
	at org.mortbay.jetty.servlet.SessionHandler.handle(SessionHandler.java:181)
	at org.mortbay.jetty.handler.ContextHandler.handle(ContextHandler.java:689)
	at org.mortbay.jetty.webapp.WebAppContext.handle(WebAppContext.java:391)
	at org.mortbay.jetty.handler.ContextHandlerCollection.handle(ContextHandlerCollection.java:146)
	at org.mortbay.jetty.handler.HandlerCollection.handle(HandlerCollection.java:114)
	at org.mortbay.jetty.handler.HandlerWrapper.handle(HandlerWrapper.java:139)
	at org.mortbay.jetty.Server.handle(Server.java:285)
	at org.mortbay.jetty.HttpConnection.handleRequest(HttpConnection.java:457)
	at org.mortbay.jetty.HttpConnection$RequestHandler.headerComplete(HttpConnection.java:751)
	at org.mortbay.jetty.HttpParser.parseNext(HttpParser.java:500)
	at org.mortbay.jetty.HttpParser.parseAvailable(HttpParser.java:209)
	at org.mortbay.jetty.HttpConnection.handle(HttpConnection.java:357)
	at org.mortbay.io.nio.SelectChannelEndPoint.run(SelectChannelEndPoint.java:329)
	at org.mortbay.thread.BoundedThreadPool$PoolThread.run(BoundedThreadPool.java:475)
Caused by: java.lang.IllegalArgumentException: object is not an instance of declaring class
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
	at java.lang.reflect.Method.invoke(Method.java:597)
	at org.apache.myfaces.view.facelets.tag.BeanPropertyTagRule$LiteralPropertyMetadata.applyMetadata(BeanPropertyTagRule.java:84)
	... 55 more

 *
 *
 */

/**
 * Simple component to be picked up by
 */
@FacesComponent("at.irian.JavaTestComponent")
public class JavaTestComponent extends UIInput implements Markable {

    String _testAttr;


    enum PropertyKeys {
        inc, testAttr, testAttr2
    }

    public JavaTestComponent() {
        setRendererType("at.irian.JavaTestRenderer");
    }

    
    public String getMarker() {
        return "<h2>Component 1 marker</h2>";
    }

    public void setMarker() {
       
    }
    
    public int getInc() {
        return (Integer) getStateHelper().eval(PropertyKeys.inc, 1);
    }

    public void setInc(int inc) {
        getStateHelper().put(PropertyKeys.inc, inc);
    }

    public String getTestAttr() {
        return (String) getStateHelper().eval(PropertyKeys.testAttr, "");
    }

    public void setTestAttr(String testAttr) {
        getStateHelper().put(PropertyKeys.testAttr, testAttr);
    }

      public String getTestAttr2() {
        return (String) getStateHelper().eval(PropertyKeys.testAttr, "");
    }

    public void setTestAttr2(String testAttr) {
        getStateHelper().put(PropertyKeys.testAttr2, testAttr);
    }
}
