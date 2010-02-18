package org.apache.myfaces.javaloader.blog;

/**
 * Created by IntelliJ IDEA.
 * User: werpu2
 * Date: 18.02.2010
 * Time: 14:19:42
 * To change this template use File | Settings | File Templates.
 */
public class Cyclic1 {
    Cyclic2 cycl2 = null;

    public Cyclic2 getCycl2() {
        return cycl2;
    }

    public void setCycl2(Cyclic2 cycl2) {
        this.cycl2 = cycl2;
    }
}
