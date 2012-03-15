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
package org.apache.myfaces.extensions.scripting.jsf.dynamicDecorators.implementations;

import org.apache.myfaces.extensions.scripting.core.api.Decorated;
import org.apache.myfaces.extensions.scripting.core.api.ScriptingConst;
import org.apache.myfaces.extensions.scripting.core.api.WeavingContext;

import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.render.ClientBehaviorRenderer;
import javax.faces.render.RenderKit;
import javax.faces.render.Renderer;
import javax.faces.render.ResponseStateManager;
import java.io.OutputStream;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Iterator;

/**
 * Weaving renderkit which
 * acts as a proxy factory for
 * our internal reloading referers
 *
 * @author Werner Punz
 */
public class RenderkitProxy extends RenderKit implements Decorated
{

    RenderKit _delegate = null;

    public RenderkitProxy(RenderKit delegate)
    {
        _delegate = delegate;
    }

    public void addRenderer(String componentFamily, String rendererType, Renderer renderer)
    {
        weaveDelegate();
        //wo do it brute force here because we have sometimes casts and hence cannot rely on proxies
        //renderers itself are flyweight patterns which means they are shared over objects

        renderer = (Renderer) reloadInstance(renderer, ScriptingConst.ARTIFACT_TYPE_RENDERER);

        _delegate.addRenderer(componentFamily, rendererType, renderer);
    }

    /**
     * unproxy renderer from different systems
     *
     * @return
     */
    private Renderer unproxy(Renderer proxiedObject)
    {
        Renderer oldProxiedObject = proxiedObject;
        try
        {
            //extval

            String name = proxiedObject.getClass().getName();
            while (name.contains("ExtVal") && (name.contains("Wrapper") || name.contains("Proxy")))
            {
                Field proxiedField = proxiedObject.getClass().getDeclaredField("wrapped");
                proxiedField.setAccessible(true);
                proxiedObject = (Renderer) proxiedField.get(proxiedObject);

                name = proxiedObject.getClass().getName();
            }

            // "getWrapped");
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (NoSuchFieldException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File
            // Templates.
        }
        return proxiedObject;
    }

    private Renderer proxy(Renderer proxy, Renderer toBeProxied)
    {
            Renderer oldProxiedObject = proxy;
           try
           {
               //extval
   
               String name = proxy.getClass().getName();
               while (name.contains("ExtVal") && (name.contains("Wrapper") || name.contains("Proxy")))
               {
                   Field proxiedField = proxy.getClass().getDeclaredField("wrapped");
                   proxiedField.setAccessible(true);
                   oldProxiedObject = proxy;
                   proxy = (Renderer) proxiedField.get(proxy);
                   name = proxy.getClass().getName();
                   if(!name.contains("ExtVal") && !(name.contains("Wrapper") || name.contains("Proxy"))) {
                       proxiedField.set(oldProxiedObject, proxy);
                       return proxy;
                   }

               }
           }
           catch (IllegalAccessException e)
           {
               e.printStackTrace();
           }
           catch (NoSuchFieldException e)
           {
               e.printStackTrace();
           }
           return  toBeProxied;
    }
    public Renderer getRenderer(String componentFamily, String rendererType)
    {
        weaveDelegate();
        Renderer rendr = _delegate.getRenderer(componentFamily, rendererType);
        Renderer unproxiedRendr = unproxy(rendr);
        if(unproxiedRendr.getClass().getName().contains("JavaTestRenderer1")) {
            System.out.println("Debugpoint found");
        }
        //TODO extval proxy handling here
        Renderer rendr2 = (Renderer) reloadInstance(unproxiedRendr, ScriptingConst.ARTIFACT_TYPE_RENDERER);
        if (unproxiedRendr != rendr2)
        {
            Renderer tempRenderer = _delegate.getRenderer(componentFamily, rendererType);
            /**<></>if (tempRenderer instanceof PurgedRenderer) {
             return handleAnnotationChange(componentFamily, rendererType);
             } */
            //in case of a renderer proxy we have to weave the original object back in
            rendr2 = proxy(rendr ,rendr2);
            _delegate.addRenderer(componentFamily, rendererType, rendr2);
            return rendr2;
        }
        return rendr;
    }

    private ClientBehaviorRenderer handleAnnotationChangeBehaviorRenderer(String s)
    {
        ClientBehaviorRenderer rendr2;

        rendr2 = _delegate.getClientBehaviorRenderer(s);
        /*<>if (rendr2 instanceof PurgedClientBehaviorRenderer) {
            throw new FacesException("Renderer not found");
        } */
        rendr2 = _delegate.getClientBehaviorRenderer(s);
        return rendr2;
    }

    private Renderer handleAnnotationChange(String s, String s1)
    {
        Renderer rendr2;

        //WeavingContext.getWeaver().fullClassScan();
        rendr2 = _delegate.getRenderer(s, s1);
        /*<>if (rendr2 instanceof PurgedRenderer) {
            throw new FacesException("Renderer not found");
        }*/
        rendr2 = _delegate.getRenderer(s, s1);
        return rendr2;
    }

    public ResponseStateManager getResponseStateManager()
    {
        weaveDelegate();
        return _delegate.getResponseStateManager();
    }

    public ResponseWriter createResponseWriter(Writer writer, String s, String s1)
    {
        weaveDelegate();
        return (ResponseWriter) reloadInstance(_delegate.createResponseWriter(writer, s, s1), ScriptingConst.ARTIFACT_TYPE_RESPONSEWRITER);
    }

    public ResponseStream createResponseStream(OutputStream outputStream)
    {
        weaveDelegate();
        return (ResponseStream) reloadInstance(_delegate.createResponseStream(outputStream), ScriptingConst.ARTIFACT_TYPE_RESPONSESTREAM);
    }

    @Override
    public void addClientBehaviorRenderer(String s, ClientBehaviorRenderer renderer)
    {

        weaveDelegate();
        renderer = (ClientBehaviorRenderer) reloadInstance(renderer, ScriptingConst.ARTIFACT_TYPE_CLIENTBEHAVIORRENDERER);
        _delegate.addClientBehaviorRenderer(s, renderer);
    }

    @Override
    public ClientBehaviorRenderer getClientBehaviorRenderer(String s)
    {
        weaveDelegate();
        ClientBehaviorRenderer rendr = _delegate.getClientBehaviorRenderer(s);
        ClientBehaviorRenderer rendr2 = (ClientBehaviorRenderer) reloadInstance(rendr, ScriptingConst.ARTIFACT_TYPE_CLIENTBEHAVIORRENDERER);
        if (rendr != rendr2)
        {

            rendr2 = _delegate.getClientBehaviorRenderer(s);
            /*<>if (rendr2 instanceof PurgedClientBehaviorRenderer) {
                return handleAnnotationChangeBehaviorRenderer(s);
            }*/
            return rendr2;
        }
        return rendr;
    }

    @Override
    public Iterator<String> getClientBehaviorRendererTypes()
    {
        weaveDelegate();
        return _delegate.getClientBehaviorRendererTypes();
    }

    @Override
    public Iterator<String> getComponentFamilies()
    {
        weaveDelegate();
        return _delegate.getComponentFamilies();
    }

    @Override
    public Iterator<String> getRendererTypes(String s)
    {
        weaveDelegate();
        return _delegate.getRendererTypes(s);
    }

    public Object getDelegate()
    {
        return _delegate;
    }

    private void weaveDelegate()
    {
        _delegate = (RenderKit) WeavingContext.getInstance().reload(_delegate, ScriptingConst.ARTIFACT_TYPE_RENDERKIT);
    }

    private Object reloadInstance(Object instance, int artefactType)
    {
        if (instance == null)
        {
            return null;
        }
        if (WeavingContext.getInstance().isDynamic(instance.getClass()))
        {
            instance = WeavingContext.getInstance().reload(instance, artefactType);
            //now the add should be done properly if possible
        }
        return instance;
    }

}
