package org.apache.myfaces.javaloader.test;

import org.apache.myfaces.scripting.loaders.java.ScriptingClass;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;

@ManagedBean (name="javatestbean")
@SessionScoped
public class TestBean3 {
    String sayHello = "hello worldgggg";
    String hello2 = "hello from added attribute";
    String hello3 = "hello from  added attribute 2";

    public String getSayHello() {
        return "Java dynamic  bean Testbean3 bla bla bla - "+TestClass2.hello2 + hello3;
    }

    public String getSayHello2() {
        return hello2;
    }


    public void setSayHello(String hello) {
        this.sayHello = hello;
        System.out.println("hello world");
    }



}