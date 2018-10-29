package com.cs.sicnu.core.utils;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public interface ByteAcess {

    void append(ByteBuffer buffer) throws IllegalAccessException;
    void append(byte[] bytes,int offset,int len) throws IllegalAccessException;

    void mark(int cur);
    void forward(int offset);
    void clear();
    int find(byte[] str);
    int find(byte[] str,int offset);
    int getBound();

    String getString(int begin, int end, Charset charset);
    byte[] getRangeBytes(int offset,int len);
}
