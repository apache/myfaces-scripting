package org.apache.myfaces.groovyloader.test
/**
 * Created by IntelliJ IDEA.
 * User: werpu
 * Date: 09.05.2008
 * Time: 15:05:13
 * To change this template use File | Settings | File Templates.
 */
class TestBean {
    String helloworld = "hallo ist - die bean"
    def helloworld_changed = true;

    public String getHelloworld() {
        return helloworld
    }


    public String doit() {
        print "doit S "
        return null
    }


    public String getXxx() {
        "Xxx"
    }
}