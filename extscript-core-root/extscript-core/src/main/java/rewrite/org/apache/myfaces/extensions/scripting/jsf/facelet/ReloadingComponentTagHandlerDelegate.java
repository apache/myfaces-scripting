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

package rewrite.org.apache.myfaces.extensions.scripting.jsf.facelet;

import org.apache.myfaces.view.facelets.tag.jsf.ActionSourceRule;
import org.apache.myfaces.view.facelets.tag.jsf.ComponentTagHandlerDelegate;
import org.apache.myfaces.view.facelets.tag.jsf.EditableValueHolderRule;
import org.apache.myfaces.view.facelets.tag.jsf.ValueHolderRule;
import rewrite.org.apache.myfaces.extensions.scripting.core.api.ScriptingConst;
import rewrite.org.apache.myfaces.extensions.scripting.core.api.WeavingContext;
import rewrite.org.apache.myfaces.extensions.scripting.jsf.facelet.support.ComponentRule;
import rewrite.org.apache.myfaces.extensions.scripting.jsf.facelet.support.SwitchingMetarulesetImpl;

import javax.faces.component.ActionSource;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.MetaRuleset;
import javax.faces.view.facelets.TagHandlerDelegate;
import java.io.IOException;

/**
 * we provide our own component tag handler factory impl
 * so that we can deal with refreshing of components
 * on Facelets level without running into
 * nasty type exceptions
 */
public class ReloadingComponentTagHandlerDelegate extends TagHandlerDelegate {

    ComponentHandler _owner;
    TagHandlerDelegate _delegate;

    public ReloadingComponentTagHandlerDelegate(ComponentHandler owner) {
        applyOwner(owner);
    }

    private void applyOwner(ComponentHandler owner) {
        _owner = owner;
        _delegate = new ComponentTagHandlerDelegate(_owner);
    }

    @Override
    public void apply(FaceletContext ctx, UIComponent comp) throws IOException {
        if (WeavingContext.getInstance().isDynamic(_owner.getClass())) {
            ComponentHandler newOwner = (ComponentHandler) WeavingContext.getInstance().reload(_owner,
                    ScriptingConst.ARTIFACT_TYPE_COMPONENT_HANDLER);
            if (!newOwner.getClass().equals(_owner.getClass())) {
                applyOwner(newOwner);
            }
        }
        _delegate.apply(ctx, comp);
    }

    public MetaRuleset createMetaRuleset(Class type) {
        //We have to create a different meta rule set for dynamic classes
        //which have weaver instantiation criteria, the original meta rule set
        //first applies the attributes and then calls BeanPropertyTagRule
        //that one however caches the current method and does not take into consideration
        //that classes can be changed on the fly

        // if (WeavingContext.isDynamic(type)) {
        MetaRuleset m = new SwitchingMetarulesetImpl(_owner.getTag(), type);
        // ignore standard component attributes
        m.ignore("binding").ignore("id");

        // add auto wiring for attributes
        m.addRule(ComponentRule.Instance);

        // if it's an ActionSource
        if (ActionSource.class.isAssignableFrom(type)) {
            m.addRule(ActionSourceRule.INSTANCE);
        }

        // if it's a ValueHolder
        if (ValueHolder.class.isAssignableFrom(type)) {
            m.addRule(ValueHolderRule.INSTANCE);

            // if it's an EditableValueHolder
            if (EditableValueHolder.class.isAssignableFrom(type)) {
                m.ignore("submittedValue");
                m.ignore("valid");
                m.addRule(EditableValueHolderRule.INSTANCE);
            }
        }

        return m;
        //}

        //return _delegate.createMetaRuleset(type);
    }
}
