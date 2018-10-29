package com.sicnu.cs.http;

public interface HttpRequestHandler {
    void handleRequset(HttpConnection connection,HttpRequest request,HttpResponse response);
}
