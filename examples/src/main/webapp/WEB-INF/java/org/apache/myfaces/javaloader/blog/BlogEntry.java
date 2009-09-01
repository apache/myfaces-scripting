package org.apache.myfaces.javaloader.blog;

/**
 * @author werpu2
 * @date: 01.09.2009
 */
public class BlogEntry {

    String firstName = "";
    
    String lastName = "";
    String topic = "";

    String content = "";


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
