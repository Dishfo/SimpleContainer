package com.sicnu.cs.http;

public interface HttpParseListener {

    void onStartParse(HttpRequest request);
    void onException(Exception e,int state);
    void onCompete(HttpRequest request);
}
