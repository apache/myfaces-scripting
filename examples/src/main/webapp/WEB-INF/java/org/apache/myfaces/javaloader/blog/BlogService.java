package org.apache.myfaces.javaloader.blog;

import org.apache.myfaces.scripting.loaders.java.ScriptingClass;

import java.util.List;
import java.util.LinkedList;
import java.util.Collections;

/**
 * @author werpu2
 * @date: 01.09.2009
 */
@ScriptingClass
public class BlogService {

    /**
     * note we cannot cast on dynamically referenced
     * and recompiled objects which are shared between beans
     * because due to dynamic recompilation
     *
     * Object a->references b does not reference b of the same class
     * as object c->references b, we have to use introspection in this case
     * we can use our utils class to make it a tiny bit more comfortable
     *
     * Statically compiled types always stay the same however
     * the same goes for interfaces which are present as compiled code only
     * 
     */
    List<Object> blogEntries = Collections.synchronizedList(new LinkedList<Object>());

    public void addEntry(Object entry) {
        if(entry != null) {
            blogEntries.add(entry);
        }
    }


    public List<Object> getBlogEntries() {
        return blogEntries;
    }

    public void setBlogEntries(List<Object> blogEntries) {
        this.blogEntries = blogEntries;
    }
}
