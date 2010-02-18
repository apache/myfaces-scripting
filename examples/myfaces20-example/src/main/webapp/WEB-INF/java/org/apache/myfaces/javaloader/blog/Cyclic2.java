package org.apache.myfaces.javaloader.blog;

/**
 * Created by IntelliJ IDEA.
 * User: werpu2
 * Date: 18.02.2010
 * Time: 14:19:51
 * To change this template use File | Settings | File Templates.
 */
public class Cyclic2 {

    static Cyclic2 instance = null;

    Cyclic1 cycl1 = null;

    public Cyclic1 getCycl1() {
        return cycl1;
    }

    public void setCycl1(Cyclic1 cycl1) {
        this.cycl1 = cycl1;
    }

    public static Cyclic2 getInstance() {
        return instance;
    }

    public static void setInstance(Cyclic2 instance) {
        Cyclic2.instance = instance;
    }
}
