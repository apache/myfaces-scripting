package org.apache.myfaces.scripting.scratchpad.probes;

import org.apache.myfaces.scripting.scratchpad.probes.MyPersonalClass3;
import org.apache.myfaces.scripting.scratchpad.javaReloading.ITestClass;

/**
 * Testclass which will be recompiled by the
 * jcl (for now)
 */
public class TestClass implements ITestClass {

    String hello = "aaaa 222";

    
    public void helloWorld() {
        System.out.println((new MyPersonalClass3()).getHello());
        System.out.println("hello from werner ssss");
    }

}
