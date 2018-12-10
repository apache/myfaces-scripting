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

package org.apache.myfaces.extensions.scripting.jsf.facelet;


import org.apache.myfaces.view.facelets.tag.jsf.ConverterTagHandlerDelegate;
import org.apache.myfaces.extensions.scripting.core.api.ScriptingConst;
import org.apache.myfaces.extensions.scripting.core.api.WeavingContext;

import javax.faces.component.UIComponent;
import javax.faces.view.facelets.ConverterHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.MetaRuleset;
import javax.faces.view.facelets.TagHandlerDelegate;
import java.io.IOException;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

public class ReloadingConverterTagHandlerDelegate extends TagHandlerDelegate {

        ConverterHandler _owner;
        TagHandlerDelegate _delegate;

        public ReloadingConverterTagHandlerDelegate(ConverterHandler owner) {
            applyOwner(owner);
        }

    private void applyOwner(ConverterHandler owner) {
        _owner = owner;
        _delegate = new ConverterTagHandlerDelegate(_owner);
    }

    @Override
        public void apply(FaceletContext ctx, UIComponent comp) throws IOException {
            if (WeavingContext.getInstance().isDynamic(_owner.getClass())) {
                ConverterHandler newOwner = (ConverterHandler) WeavingContext.getInstance().reload(_owner,
                        ScriptingConst.ARTIFACT_TYPE_CONVERTER_HANDLER);
                if(!newOwner.getClass().equals(_owner.getClass())) {
                    applyOwner(newOwner);
                }
            }
            _delegate.apply(ctx, comp);
        }

        @Override
        public MetaRuleset createMetaRuleset(Class type) {
            return _delegate.createMetaRuleset(type);
        }

}
