package com.sicnu.cs.http.encode;

import java.io.ByteArrayOutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ZlibCompress implements CompressHandler {

    @Override
    public byte[] encode(byte[] bytes) {
        ByteArrayOutputStream arrayOutputStream=
                new ByteArrayOutputStream();
        Deflater deflater=new Deflater(1);
        deflater.setInput(bytes);
        deflater.finish();
        byte[] cache=new byte[1024];
        int len=0;
        while (!deflater.finished()){
            len=deflater.deflate(cache);
            arrayOutputStream.write(cache,0,len);
        }
        return arrayOutputStream.toByteArray();
    }

    @Override
    public byte[] decode(byte[] bytes) {
        ByteArrayOutputStream arrayOutputStream=
                new ByteArrayOutputStream();

        Inflater inflater=new Inflater();
        inflater.setInput(bytes);
        byte[] cache=new byte[1024];
        int len=0;

        while (!inflater.finished()){
            try {
                len=inflater.inflate(bytes);
                arrayOutputStream.write(cache,0,len);
            } catch (DataFormatException e) {
                throw new IllegalStateException("" +
                        "error zlib data format ");
            }
        }

        return arrayOutputStream.toByteArray();
    }
}
