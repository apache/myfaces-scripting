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
package rewrite.org.apache.myfaces.extensions.scripting.jsf.annotation.purged;


import rewrite.org.apache.myfaces.extensions.scripting.core.api.Decorated;

import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.faces.render.Renderer;
import javax.faces.render.ResponseStateManager;
import java.io.OutputStream;
import java.io.Writer;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class PurgedRenderkit extends RenderKit implements Decorated
{

    private static final String DOES_NOT_EXIST = "Renderkit does not exist";

    RenderKit _delegate;

    public PurgedRenderkit(RenderKit delegate) {
        _delegate = delegate;
    }

    @Override
    public void addRenderer(String family, String rendererType, Renderer renderer) {
        throw new RuntimeException(DOES_NOT_EXIST);
    }

    @Override
    public ResponseStream createResponseStream(OutputStream out) {
        throw new RuntimeException(DOES_NOT_EXIST);
    }

    @Override
    public ResponseWriter createResponseWriter(Writer writer, String contentTypeList, String characterEncoding) {
        throw new RuntimeException(DOES_NOT_EXIST);
    }

    @Override
    public Renderer getRenderer(String family, String rendererType) {
        throw new RuntimeException(DOES_NOT_EXIST);
    }

    @Override
    public ResponseStateManager getResponseStateManager() {
        throw new RuntimeException(DOES_NOT_EXIST);
    }

    public RenderKit getDelegate() {
        return _delegate;
    }

    public void setDelegate(RenderKit delegate) {
        _delegate = delegate;
    }
}
