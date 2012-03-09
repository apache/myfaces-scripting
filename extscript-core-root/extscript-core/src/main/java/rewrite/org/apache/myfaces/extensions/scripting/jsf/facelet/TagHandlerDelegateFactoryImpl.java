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


import org.apache.myfaces.view.facelets.tag.jsf.BehaviorTagHandlerDelegate;
import org.apache.myfaces.view.facelets.tag.jsf.ConverterTagHandlerDelegate;
import org.apache.myfaces.view.facelets.tag.jsf.ValidatorTagHandlerDelegate;
import rewrite.org.apache.myfaces.extensions.scripting.core.context.WeavingContext;

import javax.faces.view.facelets.*;

/**
 * Tag handler delegate factory which injects reloading
 * proxies for our facelet artifacts
 */
public class TagHandlerDelegateFactoryImpl extends TagHandlerDelegateFactory {

    @Override
    public TagHandlerDelegate createBehaviorHandlerDelegate(
            BehaviorHandler owner) {
        if (WeavingContext.getInstance().isDynamic(owner.getClass())) {
            return new ReloadingBehaviorTagHandlerDelegate(owner);
        } else {
            return new BehaviorTagHandlerDelegate(owner);
        }
    }

    @Override
    public TagHandlerDelegate createComponentHandlerDelegate(
            ComponentHandler owner) {
        return new ReloadingComponentTagHandlerDelegate(owner);
    }

    @Override
    public TagHandlerDelegate createConverterHandlerDelegate(
            ConverterHandler owner) {
        if (WeavingContext.getInstance().isDynamic(owner.getClass())) {
            return new ReloadingConverterTagHandlerDelegate(owner);
        } else {
            return new ConverterTagHandlerDelegate(owner);
        }
    }

    @Override
    public TagHandlerDelegate createValidatorHandlerDelegate(
            ValidatorHandler owner) {
        if (WeavingContext.getInstance().isDynamic(owner.getClass())) {
            return new ReloadingValidatorTagHandlerDelegate(owner);
        } else {
            return new ValidatorTagHandlerDelegate(owner);
        }
    }
}

