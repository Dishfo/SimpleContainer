package com.sicnu.cs.http.encode;

import com.cs.sicnu.core.protocol.HttpHeadConstant;
import com.sun.istack.internal.NotNull;

public class EncodingUtil {
    public static @NotNull byte[] encoding(byte[] bytes, String alo){
        switch (alo) {
            case HttpHeadConstant.H_CONE_GZIP:
                return new GZIPCompress().encode(bytes);
            case HttpHeadConstant.H_CONE_DEFLATE:
                return new ZlibCompress().encode(bytes);
            case HttpHeadConstant.H_CONE_COMPRESS:
                return new UnixCompress().encode(bytes);
        }
        return bytes;
    }


    public static byte[] decoding(byte[] bytes,String alo){
        switch (alo) {
            case HttpHeadConstant.H_CONE_GZIP:
                return new GZIPCompress().decode(bytes);
            case HttpHeadConstant.H_CONE_DEFLATE:
                return new ZlibCompress().decode(bytes);
            case HttpHeadConstant.H_CONE_COMPRESS:
                return new UnixCompress().decode(bytes);
        }
        return bytes;
    }
}




