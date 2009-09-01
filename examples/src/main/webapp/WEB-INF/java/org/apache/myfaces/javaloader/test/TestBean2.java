package org.apache.myfaces.javaloader.test;

import org.apache.myfaces.scripting.loaders.java.ScriptingClass;

@ScriptingClass
public class TestBean2 {
    String sayHello = "hello worldgggg";
    String hello2 = "hello from added attribute";
    String hello3 = "hello from added attribute 2";

    public String getSayHello() {
        return "hello from a Java  coded dynamic bean" + TestClass2.hello2 + hello3;
    }

    public String getSayHello2() {
        return hello2;
    }


    public void setSayHello(String hello) {
        this.sayHello = hello;
        System.out.println("hello world");
    }

}