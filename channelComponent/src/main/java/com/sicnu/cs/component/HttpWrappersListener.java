package com.sicnu.cs.component;

import com.cs.sicnu.core.protocol.Connection;
import com.cs.sicnu.core.protocol.Http11Parser;
import com.sicnu.cs.wrapper.ChannelWrapper;
import com.sicnu.cs.wrapper.HttpConnection;
import com.sicnu.cs.wrapper.SocketChannelWrapper;
import com.sicnu.cs.wrapper.WrappersListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 简要得描述借助listener的处理过程
 */
public class HttpWrappersListener implements WrappersListener {

    private Logger logger= LogManager.getLogger(getClass().getName());
    private ThreadPoolExecutor executor;

    HttpWrappersListener() {
        executor=new ThreadPoolExecutor(4,20,10, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>());
    }

    @Override
    public void onWrapperCreated(ChannelWrapper wrapper) {
        if (wrapper instanceof SocketChannelWrapper){
            HttpConnection.Builder builder=new HttpConnection.Builder()
                    .setChannelWrapper((SocketChannelWrapper) wrapper)
                    .setLoadPort(((SocketChannelWrapper) wrapper).getLocalPort())
                    .setRemote(((SocketChannelWrapper) wrapper).getRemote())
                    .setSchema(Connection.HTTP_SCHEMA)
                    .setEngine(EngineManager.getInstance())
                    .setParser(new Http11Parser());
            ((SocketChannelWrapper) wrapper).setConnection(builder.build());
        }
    }

    @Override
    public void onWrapperRead(ChannelWrapper wrapper, ByteBuffer[] data) {
        logger.debug("receive data");
        if (wrapper instanceof SocketChannelWrapper){
            final HttpConnection connection=((SocketChannelWrapper) wrapper).getConnection();
            connection.receive(data);
        }
    }

    @Override
    public void onWrapperWrite(ChannelWrapper wrapper) {
        logger.debug("channel"+wrapper+" ready to write");
    }

}
