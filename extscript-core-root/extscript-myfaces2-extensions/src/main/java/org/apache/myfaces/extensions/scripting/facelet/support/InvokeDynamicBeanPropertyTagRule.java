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
package org.apache.myfaces.extensions.scripting.facelet.support;

import org.apache.myfaces.extensions.scripting.core.util.ReflectUtil;

import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.Metadata;
import javax.faces.view.facelets.MetadataTarget;
import javax.faces.view.facelets.TagAttribute;
import java.lang.reflect.Method;

/**
 * We have to introduce a BeanPropertyTagRule
 * which calls the setter of a given component
 * on a weaker base than the original facelets component
 * property tag rule does.
 * By not enforcing a strict per object/class policy on calling
 * the setter we are able to reload the classes on the fly
 * <p/>
 * the original approach was to cache the classes, and then
 * call the invoke method on the existing class
 * if we now exchange the classes we have a problem...
 * By making the invocation of the method independend from the underlying
 * class (sort of calling an invokedynamic) we can bypass this problem
 * on facelets level.
 */
public class InvokeDynamicBeanPropertyTagRule {
    public final static InvokeDynamicBeanPropertyTagRule Instance = new InvokeDynamicBeanPropertyTagRule();

    public Metadata applyRule(String name, TagAttribute attribute, MetadataTarget meta) {
        Method m = meta.getWriteMethod(name);

        // if the property is writable
        if (m != null) {
            if (attribute.isLiteral()) {
                return new LiteralPropertyMetadata(m, attribute);
            } else {
                return new DynamicPropertyMetadata(m, attribute);
            }
        }

        return null;
    }

    final static class LiteralPropertyMetadata extends Metadata {

        private final Method method;

        private final TagAttribute attribute;

        private Object[] value;

        public LiteralPropertyMetadata(Method method, TagAttribute attribute) {
            this.method = method;
            this.attribute = attribute;
        }

        public void applyMetadata(FaceletContext ctx, Object instance) {
            if (value == null) {
                String str = this.attribute.getValue();
                value = new Object[]{ctx.getExpressionFactory().coerceToType(str, method.getParameterTypes()[0])};
            }
            //What we do here is simply to call an invoke dynamic on the method with the same name
            //but on the new instance of, that way we can bypass class problems
            //because the method reference has stored the old class in our case
            ReflectUtil.executeMethod(instance, method.getName(), this.value);
        }

    }

    final static class DynamicPropertyMetadata extends Metadata {

        private final Method method;

        private final TagAttribute attribute;

        private final Class<?> type;

        public DynamicPropertyMetadata(Method method, TagAttribute attribute) {
            this.method = method;
            this.type = method.getParameterTypes()[0];
            this.attribute = attribute;
        }

        public void applyMetadata(FaceletContext ctx, Object instance) {
            ReflectUtil.executeMethod(instance, method.getName(), new Object[]{attribute.getObject(ctx, type)});
        }
    }
}
