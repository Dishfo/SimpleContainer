package com.sicnu.cs.http.encode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZIPCompress implements CompressHandler {


    @Override
    public byte[] encode(byte[] bytes) {
        ByteArrayOutputStream arrayOutputStream=
                new ByteArrayOutputStream(1024);

        try {
            GZIPOutputStream gzipOutputStream=new GZIPOutputStream(arrayOutputStream);
            gzipOutputStream.write(bytes);
            gzipOutputStream.close();
            return arrayOutputStream.toByteArray();
        } catch (IOException e) {
            return new byte[0];
        }
    }

    @Override
    public byte[] decode(byte[] bytes) {
        ByteArrayInputStream arrayInputStream=
                new ByteArrayInputStream(bytes);

        ByteArrayOutputStream arrayOutputStream=
                new ByteArrayOutputStream();
        try {
            GZIPInputStream gzipInputStream=new GZIPInputStream(arrayInputStream);
            byte[] res=new byte[1024];
            int len=0;
            while ((len=gzipInputStream.read(bytes))>0){
                arrayOutputStream.write(res,0,len);
            }
            return arrayOutputStream.toByteArray();
        } catch (IOException e) {
            return new byte[0];
        }
    }

}
