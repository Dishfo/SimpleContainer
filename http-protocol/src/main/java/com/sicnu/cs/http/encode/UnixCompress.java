package com.sicnu.cs.http.encode;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UnixCompress implements CompressHandler {
    @Override
    public byte[] encode(byte[] bytes) {
        ByteArrayOutputStream arrayOutputStream=
                new ByteArrayOutputStream();
        BZip2CompressorOutputStream compressorOutputStream=null;
        try {
            compressorOutputStream=
                    new BZip2CompressorOutputStream(arrayOutputStream);
            compressorOutputStream.write(bytes);
            compressorOutputStream.close();
        } catch (IOException e) {
            return bytes;
        }

        return arrayOutputStream.toByteArray();
    }

    @Override
    public byte[] decode(byte[] bytes) {
        ByteArrayOutputStream arrayOutputStream=
                new ByteArrayOutputStream();
        ByteArrayInputStream arrayInputStream=
                new ByteArrayInputStream(bytes);
        BZip2CompressorInputStream compressorInputStream=null;
        try {
            compressorInputStream=
                    new BZip2CompressorInputStream(arrayInputStream);
            byte[] res=new byte[1024];
            int len=0;
            while ((len=compressorInputStream.read(bytes))>0){
                arrayOutputStream.write(res,0,len);
            }
        } catch (IOException e) {
            return bytes;
        }

        return arrayOutputStream.toByteArray();
    }
}
