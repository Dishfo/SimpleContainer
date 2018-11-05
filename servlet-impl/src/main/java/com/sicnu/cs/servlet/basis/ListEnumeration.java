package com.sicnu.cs.servlet.basis;

import java.util.Enumeration;
import java.util.List;

public class ListEnumeration<T> implements Enumeration<T> {

    private List<T> list;
    private int cur;

    public ListEnumeration(List<T> list) {
        this.list = list;
        cur=0;
    }

    @Override
    public boolean hasMoreElements() {
        return cur<list.size();
    }

    @Override
    public T nextElement() {
        return list.get(cur++);
    }
}
