package com.cs.sicnu.core.utils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class BytesServletInputStream extends ServletInputStream {

    private ReadListener readListener=new DefaultReadListener();
    private ByteArrayInputStream arrayInputStream;
    private volatile boolean ready=false;

    public BytesServletInputStream(byte[] data) {
        arrayInputStream=new ByteArrayInputStream(data);
        ready=true;
    }

    @Override
    public boolean isFinished() {
        return arrayInputStream.available()<=0;
    }

    @Override
    public boolean isReady() {
        return ready;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        this.readListener=readListener;
    }

    @Override
    public int read() throws IOException {
        return arrayInputStream.read();
    }


    @Override
    public int read(byte[] b) throws IOException {
        return arrayInputStream.read(b);
    }


    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return arrayInputStream.read(b, off, len);
    }


    @Override
    public void close() throws IOException {
        arrayInputStream.close();
    }

    @Override
    public synchronized void reset() throws IOException {
        arrayInputStream.reset();
    }


    @Override
    public boolean markSupported() {
        return arrayInputStream.markSupported();
    }

    @Override
    public synchronized void mark(int readlimit) {
        arrayInputStream.mark(readlimit);
    }

    @Override
    public long skip(long n) throws IOException {
        return arrayInputStream.skip(n);
    }

    private class DefaultReadListener implements ReadListener{

        @Override
        public void onDataAvailable() throws IOException {

        }

        @Override
        public void onAllDataRead() throws IOException {

        }

        @Override
        public void onError(Throwable t) {

        }
    }
}
