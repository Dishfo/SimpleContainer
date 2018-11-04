package com.sicnu.cs.servlet.basis;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.IOException;
import java.util.Objects;

public class ByteServletInputStream extends ServletInputStream {

    private byte[] data;
    private int cur=0;
    private ReadListener listener;

    public ByteServletInputStream(byte[] data) {
        this.data = data;
    }

    @Override
    public boolean isFinished() {
        return data.length<=cur;
    }

    @Override
    public boolean isReady() {
        return data!=null;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        Objects.requireNonNull(readListener);
        listener=readListener;
        if (isFinished()){
            try {listener.onAllDataRead();} catch (IOException ignored) {}
        }else if (isReady()){
            try {listener.onDataAvailable();} catch (IOException ignored) {}
        }
    }

    @Override
    public int read() throws IOException {
        if (!isReady()){
            IOException e= new IOException("is not available");
            if (listener!=null){
                listener.onError(e);
            }
            throw e;
        }

        if (isFinished()){
            return -1;
        }

        byte b=data[cur++];
        if (cur==data.length){
            if (listener!=null){
                listener.onDataAvailable();
            }
        }
        return b;
    }
}
