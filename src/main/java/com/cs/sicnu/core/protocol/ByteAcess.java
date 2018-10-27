package com.cs.sicnu.core.protocol;

import java.nio.ByteBuffer;

public interface ByteAcess {

    void append(ByteBuffer buffer) throws IllegalAccessException;
    void append(byte[] bytes,int offset,int len) throws IllegalAccessException;

    void mark(int cur);
    void forward(int offset);
    void clear();
    int find(byte[] str);
    int find(byte[] str,int offset);
    int getBound();

    String getString(int begin,int end);
    byte[] getRangeBytes(int offset,int len);
}
