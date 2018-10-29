package com.sicnu.cs.wrapper;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;

public interface ChannelWrapper extends ReadableByteChannel,
        WritableByteChannel{

    int OPENED=1;
    int CLOSING=2;
    int CLOSED=3;


    void closeNow();

    /**
     * set write ops
     * wish to write
     * when select the keys
     * write actually
     */
    void toWrite(ByteBuffer buffer);
    void toWrite(byte[] data);
    void toWrite(byte[] data,int pos,int len);

    /**
     * put buffer into a channel
     * this method should clear buffer queue;
     */
    void write()throws WriteOpException;

    /**
     *
     */
    ByteBuffer[] read() throws ReadOpException;

    /**
     * cancel the channel interested key
     */
    void cancleKey();
    void attach(Object obj);


    SelectionKey getKey();


    /**
     *
     * @return in write queue has task
     */
    boolean hasWrite();

    InetSocketAddress getRemote();
    InetSocketAddress getHost();
    SelectionKey register(Selector selector,int ops);
    int interestOps();
    int interestOps(int ops);
    int addOps(int ops);
    int removeOps(int ops);

    void setAttributes(String name,Object val);
    Object getAttribute(String name);
}
