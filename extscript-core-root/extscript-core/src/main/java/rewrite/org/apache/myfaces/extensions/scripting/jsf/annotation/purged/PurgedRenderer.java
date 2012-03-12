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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.render.Renderer;
import java.io.IOException;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class PurgedRenderer extends Renderer {
    private static final String DOES_NOT_EXIST = "Renderer does not exist";

    public PurgedRenderer() {
        super();
    }

    @Override
    public void decode(FacesContext context, UIComponent component) {
        throw new RuntimeException(DOES_NOT_EXIST);
    }

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        throw new RuntimeException(DOES_NOT_EXIST);
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        throw new RuntimeException(DOES_NOT_EXIST);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        throw new RuntimeException(DOES_NOT_EXIST);
    }

    @Override
    public String convertClientId(FacesContext context, String clientId) {
        throw new RuntimeException(DOES_NOT_EXIST);
    }

    @Override
    public boolean getRendersChildren() {
        throw new RuntimeException(DOES_NOT_EXIST);
    }

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        throw new RuntimeException(DOES_NOT_EXIST);
    }
}
