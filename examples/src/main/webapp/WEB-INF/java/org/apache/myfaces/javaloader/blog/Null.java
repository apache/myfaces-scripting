package org.apache.myfaces.javaloader.blog;

/**
 * @author werpu2
 * @date: 01.09.2009
 */
public class Null {
    Class nulledClass = null;

    public Null(Class clazz) {
        nulledClass = clazz;
    }

    public Class getNulledClass() {
        return nulledClass;
    }
}
