package com.sicnu.cs.wrapper;

import com.cs.sicnu.core.process.Engine;
import com.cs.sicnu.core.process.ObjPoster;
import com.cs.sicnu.core.process.Poster;
import com.cs.sicnu.core.protocol.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;

/**
 * 报文解析的负责人
 * 同时负责把报文投递到下一个模块
 */

public class HttpConnection extends Connection implements
        ObjPoster<HttpRequest, HttpResponse>{

    private Logger logger = LogManager.getLogger(getClass().getName());

    private SocketChannelWrapper channelWrapper;
    private Http11Parser parser;
    private Engine engine;

    private void setChannelWrapper(SocketChannelWrapper channelWrapper) {
        this.channelWrapper = channelWrapper;
    }

    private void setParser(Http11Parser parser) {
        this.parser = parser;
    }

    @Override
    public void post(HttpRequest request) {
        request.setLocalPort(getLoadPort());
        request.setRemoteAddress(getRemote());
        request.setSchema(getSchema());
        request.setConnection(this);
        if (engine!=null){
            engine.handle(this,request);
        }
    }



    @Override
    public void receive(ByteBuffer buffer) {
        resolve(buffer);
    }

    @Override
    public void receive(ByteBuffer[] buffers) {
        for (ByteBuffer b:buffers){
            receive(b);
        }
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void resolve(ByteBuffer buffer) {
        try {
            parser.resolve(buffer);
        } catch (ParseException e) {
            logger.warn(e.toString());
        }
    }

    @Override
    public void resolve(ByteBuffer[] buffers) {
        try {
            parser.resolve(buffers);
        } catch (ParseException e) {
            logger.warn(e.toString());
        }
    }

    private static final String LINE_SPLIT="\r\n";

    @Override
    public void outPut(HttpResponse obj) {
        StringBuilder builder=new StringBuilder();
        builder.append(obj.getVersion())
                .append(" ")
                .append(obj.getStatus())
                .append(" ")
                .append(obj.getMsg())
                .append(LINE_SPLIT);

        for (Map.Entry<String,String> e:obj.getAttributies().entrySet()){
            builder.append(e.getKey())
                    .append(": ")
                    .append(e.getValue())
                    .append(LINE_SPLIT);
        }
        builder.append(LINE_SPLIT);

        byte[] head=builder.toString().getBytes();
        ByteBuffer buffer=getBuffer((int) (head.length+obj.getLen()));
        buffer.put(head);
        buffer.put(obj.getData().array(),0, (int) obj.getLen());
        //暂时先忽略body
        channelWrapper.toWrite(buffer.array());

    }

    private ByteBuffer getBuffer(int size){
        return ByteBuffer.allocate(size);
    }

    @Override
    public void close() {
        try {
            channelWrapper.close();
        } catch (IOException ignored) {}
    }

    public static class Builder {
        private InetSocketAddress remote;
        private int loadPort;

        private String schema;
        private SocketChannelWrapper channelWrapper;
        private Http11Parser parser;
        private Engine engine;


        public Builder setRemote(InetSocketAddress remote) {
            this.remote = remote;
            return this;
        }


        public Builder setLoadPort(int loadPort) {
            this.loadPort = loadPort;
            return this;
        }


        public Builder setSchema(String schema) {
            this.schema = schema;
            return this;
        }


        public Builder setChannelWrapper(SocketChannelWrapper channelWrapper) {
            this.channelWrapper = channelWrapper;
            return this;
        }


        public Builder setParser(Http11Parser parser) {
            this.parser = parser;
            return this;
        }


        public Builder setEngine(Engine engine) {
            this.engine = engine;
            return this;
        }

        public HttpConnection build() {
            HttpConnection connection = new HttpConnection();
            connection.setLoadPort(loadPort);
            connection.setChannelWrapper(channelWrapper);
            connection.setRemote(remote);
            connection.setSchema(schema);
            connection.setParser(parser);
            parser.setPoster(connection);
            connection.setEngine(engine);
            return connection;
        }
    }
}
