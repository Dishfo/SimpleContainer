package com.sicnu.cs.servlet.basis;

import java.util.Enumeration;
import java.util.Iterator;

public class IteratorWrapper<T > implements Enumeration<T> {
    private Iterator<? extends T> iterator;

    public IteratorWrapper(Iterator<? extends T> iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean hasMoreElements() {
        return iterator.hasNext();
    }

    @Override
    public T nextElement() {
        return iterator.next();
    }
}
