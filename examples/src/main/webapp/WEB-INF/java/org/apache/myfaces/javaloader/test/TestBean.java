package org.apache.myfaces.javaloader.test;

import org.apache.myfaces.javaloader.core.ScriptingClass;

@ScriptingClass
public class TestBean {
    String sayHello = "hello world";

    public String getSayHello() {
        return sayHello;
    }

    public void setSayHello(String hello) {
        this.sayHello = hello;
    }

}