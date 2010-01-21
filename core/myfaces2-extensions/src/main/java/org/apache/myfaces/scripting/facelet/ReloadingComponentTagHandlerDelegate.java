package org.apache.myfaces.scripting.facelet;

import org.apache.myfaces.scripting.core.util.WeavingContext;
import org.apache.myfaces.scripting.facelet.support.ComponentRule;
import org.apache.myfaces.scripting.facelet.support.SwitchingMetarulesetImpl;
import org.apache.myfaces.view.facelets.tag.jsf.*;

import javax.faces.component.ActionSource;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.view.facelets.*;
import javax.faces.view.facelets.ComponentHandler;
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
        _owner = owner;
        _delegate = new ComponentTagHandlerDelegate(owner);
    }

    @Override
    public void apply(FaceletContext ctx, UIComponent comp) throws IOException {
        if (WeavingContext.isDynamic(comp.getClass())) {
            //TODO hook our own component code in here
        }
        if (comp.getClass().getName().contains("JavaTestComponent")) {
            System.out.println("Debugpoint found");
        }

        _delegate.apply(ctx, comp);
    }

    public MetaRuleset createMetaRuleset(Class type) {
        //We have to create a different meta rule set for dynamic classes
        //which have weaver instantiation criteria, the original meta rule set
        //first applies the attributes and then calls BeanPropertyTagRule on our
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
                m.addRule(ActionSourceRule.Instance);
            }

            // if it's a ValueHolder
            if (ValueHolder.class.isAssignableFrom(type)) {
                m.addRule(ValueHolderRule.Instance);

                // if it's an EditableValueHolder
                if (EditableValueHolder.class.isAssignableFrom(type)) {
                    m.ignore("submittedValue");
                    m.ignore("valid");
                    m.addRule(EditableValueHolderRule.Instance);
                }
            }

            return m;
        //}

        //return _delegate.createMetaRuleset(type);
    }
}
