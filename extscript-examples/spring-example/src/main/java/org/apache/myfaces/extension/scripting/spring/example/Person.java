package org.apache.myfaces.extension.scripting.spring.example;

public class Person {

    private String name;

    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getFirstName() {
        return getName().split(" ")[0];
    }

    public String getLastName() {
        return getName().split(" ")[1];
    } 

}
