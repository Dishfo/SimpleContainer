package com.cs.sicnu.http;

import com.cs.sicnu.core.protocol.HttpRequest;
import com.sicnu.cs.wrapper.HttpConnection;

interface HttpMsgMapFactory<T extends HttpMessageMap> {
    T create(HttpRequest request, HttpConnection connection);
}
