package org.apache.myfaces.groovyloader.core

import org.apache.myfaces.scripting.api.ReloadingStrategy
import org.apache.myfaces.scripting.core.reloading.SimpleReloadingStrategy
import org.apache.myfaces.scripting.api.BaseWeaver;


public class GroovyReloadingStrategy extends SimpleReloadingStrategy {

    public GroovyReloadingStrategy(BaseWeaver weaver) {
        super(weaver);
    }

    /**
     * central algorithm which determines which property values are overwritten and which are not
     */
    protected void mapProperties(def target, def src) {
        src.properties.each {property ->
            //ok here is the algorithm, basic datatypes usually are not copied but read in anew and then overwritten
            //later on
            //all others can be manually overwritten by adding an attribute <attributename>_changed

            try {
                if (target.properties.containsKey(property.key)
                    && !property.key.equals("metaClass")        //the class information and meta class information cannot be changed
                    && !property.key.equals("class")            //otherwise we will get following error
                    // java.lang.IllegalArgumentException: object is not an instance of declaring class
                    && !(
                    target.properties.containsKey(property.key + "_changed") //||
                    //nothing further needed the phases take care of that
                    )) {
                    target.setProperty(property.key, property.value)
                }
            } catch (Exception e) {

            }
        }
    }

   
}

