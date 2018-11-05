package com.sicnu.cs.http;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/**
 *
 * after any operation is illegal
 *
 */

public interface HttpResponse {

    void setVersion(String version);

    void setHeader(String name,String val);

    void addHeader(String name,String val);

    void setStatus(int sc);

    int getStatus();

    String getHead(String name);

    OutputStream getBodyOutStream()throws IOException;

    Writer getBodyWriter()throws IOException;

    void cleanHead();

    void cleanBody();

    boolean isCommitted();

    void setHeadEncoding(String encoding);

    void addCookie(String s);

    void outPut() throws IOException;

}
