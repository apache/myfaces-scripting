package org.apache.myfaces.javaloader.test;

import org.apache.myfaces.javaloader.core.ScriptingClass;

@ScriptingClass
public class TestBean {
    String sayHello = "hello worldgggg";
    String hello2 = "hello from added attribute";

    public String getSayHello() {
        return "hello 1"+TestClass2.hello2;
    }
      public String getSayHello2() {
        return hello2;
    }

    public void setSayHello(String hello) {
        this.sayHello = hello;
        System.out.println("hello world");
    }

}