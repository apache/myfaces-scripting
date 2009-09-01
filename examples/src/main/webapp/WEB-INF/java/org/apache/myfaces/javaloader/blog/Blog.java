package org.apache.myfaces.javaloader.blog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.scripting.loaders.java.ScriptingClass;

/**
 * @author werpu2
 * @date: 01.09.2009
 */

@ScriptingClass
public class Blog {

    String title = "Hello to the myfaces dynamic blogging";
    String title1 = "You can alter the code for this small blogging application on the fly, " +
                    "you even can add new classes on the fly and Grooy will pick it up";

    String firstName = "";
    String lastName = "";
    String topic = "";

    String content = "";
    

    private Log getLog() {
        return LogFactory.getLog(this.getClass());
    }


    
    public String addEntry() {
        getLog().info("adding entry");

       Object service = JSFUtil.resolveVariable("javaBlogService");

       if (service == null) {
            getLog().error("service not found");
        } else {
            getLog().info("service found");
        }

        BlogEntry entry = new BlogEntry();
        //we now map it in the verbose way, the lean way would be to do direct introspection attribute mapping

        entry.setFirstName(firstName);
        entry.setLastName(lastName);
        entry.setTopic(topic);
        entry.setContent(content);
        
        if(service != null) {
            JSFUtil.executeMethod(service, "addEntry", new Cast(Object.class,entry));
        }


        
        //we stay on the same page
        return null;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle1() {
        return title1;
    }

    public void setTitle1(String title1) {
        this.title1 = title1;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
