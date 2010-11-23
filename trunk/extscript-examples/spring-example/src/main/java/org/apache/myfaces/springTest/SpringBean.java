package org.apache.myfaces.springTest;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("springBean")
@Scope("request")
@Lazy
public class SpringBean {
    private String value = "hello world from the spring bean";

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
