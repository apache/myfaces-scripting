package org.apache.myfaces;

public class SpringBean {
    private String value = "hello world from the spring bean";

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
