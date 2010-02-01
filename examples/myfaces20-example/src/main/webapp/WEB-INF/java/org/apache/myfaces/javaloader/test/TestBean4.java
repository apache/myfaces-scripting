package org.apache.myfaces.javaloader.test;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;


@ManagedBean(name = "javatestbean4")
@SessionScoped

public class TestBean4 {
    private String hello = "hello world form bean 4";

    public String getHello() {
        return hello;
    }

    public void setHello(String hello) {
        this.hello = hello;
    }
}
