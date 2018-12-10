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

import org.apache.myfaces.view.facelets.tag.MetaRulesetImpl;
import org.apache.myfaces.view.facelets.tag.MetadataImpl;
import org.apache.myfaces.view.facelets.tag.MetadataTargetImpl;
import org.apache.myfaces.view.facelets.util.ParameterCheck;

import javax.faces.view.facelets.*;
import java.beans.IntrospectionException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * we have to to do a full reimplementation of the rule set
 * because otherwise we could not plant the switching bean reloading
 * rule due to private props in the original code
 */
public class SwitchingMetarulesetImpl extends MetaRuleset {
    private final static Metadata NONE = new NullMetadata();

    //private final static Logger log = Logger.getLogger("facelets.tag.meta");
    private final static Logger log = Logger.getLogger(MetaRulesetImpl.class.getName());

    private final static WeakHashMap<String, MetadataTarget> _metadata = new WeakHashMap<String, MetadataTarget>();

    private final Map<String, TagAttribute> _attributes;

    private final List<Metadata> _mappers;

    private final List<MetaRule> _rules;

    private final Tag _tag;

    private final Class<?> _type;

    public SwitchingMetarulesetImpl(Tag tag, Class<?> type) {
        _tag = tag;
        _type = type;
        _attributes = new HashMap<String, TagAttribute>();
        _mappers = new ArrayList<Metadata>();
        _rules = new ArrayList<MetaRule>();

        // setup attributes
        for (TagAttribute attribute : _tag.getAttributes().getAll()) {
            _attributes.put(attribute.getLocalName(), attribute);
        }

        // add default rules
        _rules.add(SwitchingBeanPropertyTagRule.Instance);
    }

    public MetaRuleset add(Metadata mapper) {
        ParameterCheck.notNull("mapper", mapper);

        if (!_mappers.contains(mapper)) {
            _mappers.add(mapper);
        }

        return this;
    }

    public MetaRuleset addRule(MetaRule rule) {
        ParameterCheck.notNull("rule", rule);

        _rules.add(rule);

        return this;
    }

    public MetaRuleset alias(String attribute, String property) {
        ParameterCheck.notNull("attribute", attribute);
        ParameterCheck.notNull("property", property);

        TagAttribute attr = (TagAttribute) _attributes.remove(attribute);
        if (attr != null) {
            _attributes.put(property, attr);
        }

        return this;
    }

    public Metadata finish() {
        assert !_rules.isEmpty();

        if (!_attributes.isEmpty()) {
            MetadataTarget target = this._getMetadataTarget();
            int ruleEnd = _rules.size() - 1;

            // now iterate over attributes
            for (Map.Entry<String, TagAttribute> entry : _attributes.entrySet()) {
                Metadata data = null;

                int i = ruleEnd;

                // First loop is always safe
                do {
                    MetaRule rule = _rules.get(i);
                    data = rule.applyRule(entry.getKey(), entry.getValue(), target);
                    i--;
                } while (data == null && i >= 0);

                if (data == null) {
                    if (log.isLoggable(Level.SEVERE)) {
                        log.severe(entry.getValue() + " Unhandled by MetaTagHandler for type " + _type.getName());
                    }
                } else {
                    _mappers.add(data);
                }
            }
        }

        if (_mappers.isEmpty()) {
            return NONE;
        } else {
            return new MetadataImpl(_mappers.toArray(new Metadata[_mappers.size()]));
        }
    }

    public MetaRuleset ignore(String attribute) {
        ParameterCheck.notNull("attribute", attribute);

        _attributes.remove(attribute);

        return this;
    }

    public MetaRuleset ignoreAll() {
        _attributes.clear();

        return this;
    }

    private final MetadataTarget _getMetadataTarget() {
        String key = _type.getName();

        MetadataTarget meta = _metadata.get(key);
        if (meta == null) {
            try {
                meta = new MetadataTargetImpl(_type);
            }
            catch (IntrospectionException e) {
                throw new TagException(_tag, "Error Creating TargetMetadata", e);
            }

            _metadata.put(key, meta);
        }

        return meta;
    }

    private static class NullMetadata extends Metadata {
        /**
         * {@inheritDoc}
         */
        @Override
        public void applyMetadata(FaceletContext ctx, Object instance) {
            // do nothing
        }
    }
}
