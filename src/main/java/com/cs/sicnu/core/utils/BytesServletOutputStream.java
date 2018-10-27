package com.cs.sicnu.core.utils;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BytesServletOutputStream extends ServletOutputStream {

    private ByteArrayOutputStream realStream;

    public BytesServletOutputStream(int size) {
        realStream=new ByteArrayOutputStream(size);
    }

    public byte[] toByteArray(){
        return realStream.toByteArray();
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {}


    @Override
    public void write(int b) throws IOException {
        realStream.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        realStream.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        realStream.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        realStream.flush();
    }

    @Override
    public void close() throws IOException {
        realStream.close();
    }
}
