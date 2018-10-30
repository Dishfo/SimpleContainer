package com.sicnu.cs.http.encode;

public interface CompressHandler {

    byte[] encode(byte[] bytes);

    byte[] decode(byte[] bytes);

}
