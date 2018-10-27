package com.sicnu.cs.wrapper;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;

public interface ChannelWrapper extends ReadableByteChannel,
        WritableByteChannel{

    /**
     * life state
     */
    int CREATED=0x1;
    int CONNECTING=0x2;
    int OPENED=0X3;
    int CLOSING=0X4;
    int CLOSED=0X5;


    /**
     * work state
     */
    int UNAVAILABLE=0x11;
    int IDLE=0x12;
    int READING=0x13;
    int WRITTING=0x14;


    int getLifeState();

    int getWorkState();

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
    void write()throws ClosedChannelException;

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

    boolean forbidWrite();

    boolean forbidRead();

    void connect() throws IOException;

    SelectionKey register(Selector selector,int ops);
    int interestOps();
    int interestOps(int ops);
    int addOps(int ops);
    int removeOps(int ops);

}
