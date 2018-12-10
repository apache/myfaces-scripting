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
package org.apache.myfaces.extensions.scripting.jsf.facelet.support;

import org.apache.myfaces.view.facelets.tag.BeanPropertyTagRule;
import org.apache.myfaces.extensions.scripting.core.api.WeavingContext;

import javax.faces.view.facelets.MetaRule;
import javax.faces.view.facelets.Metadata;
import javax.faces.view.facelets.MetadataTarget;
import javax.faces.view.facelets.TagAttribute;

/**
 * Bean property tag rule
 * which switches between the fast static
 * version and the slower invoke dynamic
 * version depending on the class type of
 * the incoming instance
 */
public class SwitchingBeanPropertyTagRule extends MetaRule {

    InvokeDynamicBeanPropertyTagRule _invokeDynamic = InvokeDynamicBeanPropertyTagRule.Instance;
    BeanPropertyTagRule _invokeStatic = BeanPropertyTagRule.INSTANCE;

    public static volatile SwitchingBeanPropertyTagRule Instance = new SwitchingBeanPropertyTagRule();

    @Override
    public Metadata applyRule(String name, TagAttribute attribute, MetadataTarget meta) {
        if (WeavingContext.getInstance().isDynamic(meta.getTargetClass())) {
            return _invokeDynamic.applyRule(name, attribute, meta);
        } else {
            return _invokeStatic.applyRule(name, attribute, meta);
        }
    }
}
