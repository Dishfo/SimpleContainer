package com.sicnu.cs.servlet.basis;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.IOError;
import java.io.IOException;
import java.io.OutputStream;

public class ByteServletOutputStream extends ServletOutputStream {

    private OutputStream outputStream;
    private WriteListener listener;

    public ByteServletOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public boolean isReady() {
        return outputStream!=null;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
        listener=writeListener;
        if (listener!=null){
            if (isReady()){
                try {listener.onWritePossible();} catch (IOException ignored) {}
            }
        }
    }

    @Override
    public void write(int b) throws IOException {
        try {
            outputStream.write(b);
        }catch (Throwable throwable){
            listener.onError(throwable);
            throw new IOException(throwable);
        }
    }
}
