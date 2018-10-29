package com.sicnu.cs.http;

import com.cs.sicnu.core.protocol.Connection;
import com.sicnu.cs.wrapper.ChannelWrapper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class HttpConnection extends Connection<ByteBuffer,ByteBuffer> {

    private ChannelWrapper wrapper;
    private HttpParser parser;
    private HttpRequestHandler handler;

    public HttpConnection(ChannelWrapper wrapper) {
        this.wrapper=wrapper;
        parser=new HttpParser(new HttpParseListenerImpl());
        schema="http";
    }

    @Override
    public void receice(ByteBuffer data) {
        parser.resolve(data);
    }

    @Override
    public void receice(ByteBuffer[] data) {
        for (ByteBuffer buffer:data){
            receice(buffer);
        }
    }


    @Override
    public void output(ByteBuffer data) {

    }

    @Override
    public void output(ByteBuffer[] data) {
        for (ByteBuffer buffer:data){
            output(buffer);
        }
    }

    @Override
    public void close() {
        try {
            wrapper.close();
        } catch (IOException ignored) {}
    }

    public void setHandler(HttpRequestHandler handler) {
        this.handler = handler;
    }

    @Override
    public InetSocketAddress getHost() {
        return wrapper.getHost();
    }

    @Override
    public InetSocketAddress getRemote() {
        return wrapper.getRemote();
    }

    private HttpResponse createResponse(HttpRequest request){
        return null;
    }

    private class HttpParseListenerImpl implements HttpParseListener{

        @Override
        public void onStartParse(HttpRequest request) {

        }

        @Override
        public void onException(Exception e, int state) {

        }

        @Override
        public void onCompete(HttpRequest request) {

            if (handler!=null){
                handler.handleRequset(HttpConnection.this,
                        request,
                        createResponse(request));
            }
        }
    }
}
