package org.apache.myfaces.scripting.facelet;

import org.apache.myfaces.scripting.core.util.WeavingContext;
import org.apache.myfaces.view.facelets.tag.BeanPropertyTagRule;

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
    BeanPropertyTagRule _invokeStatic = BeanPropertyTagRule.Instance;

    public static volatile SwitchingBeanPropertyTagRule Instance = new SwitchingBeanPropertyTagRule();

    @Override
    public Metadata applyRule(String name, TagAttribute attribute, MetadataTarget meta) {
        if (WeavingContext.isDynamic(meta.getTargetClass())) {
            return _invokeDynamic.applyRule(name, attribute, meta);
        } else {
            return _invokeStatic.applyRule(name, attribute, meta);
        }
    }
}
