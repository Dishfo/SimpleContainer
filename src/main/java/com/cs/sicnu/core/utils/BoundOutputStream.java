package com.cs.sicnu.core.utils;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 制定一个bytebuffer
 * 实现对 buffer 的操作
 * 当输入的数据超出
 */
public class BoundOutputStream extends ServletOutputStream {

    private ByteBuffer buffer;
    private byte[] bytes;

    private int cnt = 0;

    private int max_size;
    public BoundOutputStream(ByteBuffer buffer) {
        this.buffer = buffer;
        max_size = buffer.capacity();
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {}

    @Override
    public void write(int b) throws IOException {
        if (cnt >= max_size) {
            throw new IOException(" write to much data");
        }
        cnt++;
        buffer.put((byte) b);
    }

    public void clear(){
        buffer.clear();
        cnt=0;
    }

    public int getCount(){
        return cnt;
    }


}
