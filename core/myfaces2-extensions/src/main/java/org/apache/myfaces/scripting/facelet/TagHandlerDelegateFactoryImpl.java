package org.apache.myfaces.scripting.facelet;

import org.apache.myfaces.view.facelets.tag.jsf.BehaviorTagHandlerDelegate;
import org.apache.myfaces.view.facelets.tag.jsf.ComponentTagHandlerDelegate;
import org.apache.myfaces.view.facelets.tag.jsf.ConverterTagHandlerDelegate;
import org.apache.myfaces.view.facelets.tag.jsf.ValidatorTagHandlerDelegate;

import javax.faces.view.facelets.BehaviorHandler;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.ConverterHandler;
import javax.faces.view.facelets.TagHandlerDelegate;
import javax.faces.view.facelets.TagHandlerDelegateFactory;
import javax.faces.view.facelets.ValidatorHandler;


public class TagHandlerDelegateFactoryImpl extends TagHandlerDelegateFactory
{

    @Override
    public TagHandlerDelegate createBehaviorHandlerDelegate(
            BehaviorHandler owner)
    {
        return new BehaviorTagHandlerDelegate(owner);
    }

    @Override
    public TagHandlerDelegate createComponentHandlerDelegate(
            ComponentHandler owner)
    {
        return new ReloadingComponentTagHandlerDelegate(owner);
    }

    @Override
    public TagHandlerDelegate createConverterHandlerDelegate(
            ConverterHandler owner)
    {
        return new ConverterTagHandlerDelegate(owner);
    }

    @Override
    public TagHandlerDelegate createValidatorHandlerDelegate(
            ValidatorHandler owner)
    {
        return new ValidatorTagHandlerDelegate(owner);
    }

}

