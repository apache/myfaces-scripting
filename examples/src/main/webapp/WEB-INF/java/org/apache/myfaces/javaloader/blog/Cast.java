package org.apache.myfaces.javaloader.blog;

/**
 * @author werpu2
 * @date: 01.09.2009
 */
public class Cast {

    Class clazz;
    Object value;

    public Cast(Class clazz, Object value) {
        this.clazz = clazz;
        this.value = value;
    }

    public Class getClazz() {
        return clazz;
    }

    public Object getValue() {
        return value;
    }
}
