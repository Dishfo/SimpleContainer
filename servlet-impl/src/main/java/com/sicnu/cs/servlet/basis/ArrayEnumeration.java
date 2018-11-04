package com.sicnu.cs.servlet.basis;

import java.util.Enumeration;

public class ArrayEnumeration<T> implements Enumeration<T> {

    private T[] array;
    private int cur;

    public ArrayEnumeration(T[] array) {
        this.array = array;
        cur=0;
    }

    @Override
    public boolean hasMoreElements() {
        return cur>=array.length;
    }

    @Override
    public T nextElement() {
        return array[cur++];
    }
}
