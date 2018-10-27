package com.sicnu.cs.wrapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * un-thread-safe
 */
public class SocketChannelWrapper implements ChannelWrapper{

    private Logger logger = LogManager.getLogger(getClass().getName());
    private HttpConnection connection;

    private AtomicInteger lifestate;
    private AtomicInteger workstate;
    private SocketChannel channel;
    private SelectionKey key;

    private AtomicBoolean iswritting=new AtomicBoolean(false);
    private LinkedBlockingQueue<ByteBuffer> towrite;

    SocketChannelWrapper(SocketChannel channel) {
        this.channel = channel;
        lifestate = new AtomicInteger(CREATED);
        workstate = new AtomicInteger(UNAVAILABLE);
        towrite = new LinkedBlockingQueue<>();
    }

    public int getLifeState() {
        return lifestate.get();
    }

    public int getWorkState() {
        return workstate.get();
    }

    public void closeNow() {
        key.cancel();
        try {
            channel.close();
        } catch (IOException e) {
            logger.info("error when closeNoW()");
        }
    }

    private void wakeupSelector(){
        key.selector().wakeup();
    }

    public void toWrite(ByteBuffer buffer) {
        towrite.add(buffer);
        addOps(SelectionKey.OP_WRITE);
        wakeupSelector();
    }

    public void toWrite(byte[] data) {
        towrite.add(ByteBuffer.wrap(data));
        addOps(SelectionKey.OP_WRITE);
        wakeupSelector();
    }

    public void toWrite(byte[] data, int pos, int len) {
        towrite.add(ByteBuffer.wrap(data, pos, len));
        addOps(SelectionKey.OP_WRITE);
        wakeupSelector();
    }


    public void write() throws ClosedChannelException {
        for (ByteBuffer buffer : towrite) {
            try {
                channel.write(buffer);
            } catch (IOException e) {
                if (e instanceof ClosedChannelException) {
                    workstate.set(UNAVAILABLE);
                    throw (ClosedChannelException) e;
                }
            } finally {
                try {
                    channel.socket().getOutputStream().flush();
                } catch (IOException e) {
                    logger.warn("write go wrong ");
                    closeNow();
                }

            }
        }
        towrite.clear();
        removeOps(SelectionKey.OP_WRITE);
    }

    public ByteBuffer[] read() throws ReadOpException {
        List<ByteBuffer> list = new ArrayList<>();
        int len = 0;
        ByteBuffer buffer = getBuffer();
        try {
            while ((len = channel.read(buffer)) > 0) {
                list.add(buffer);
                buffer.flip();
                buffer = getBuffer();
            }
            if (len == -1) {
                throw new ReadOpException("", this);
            }
        } catch (IOException e) {
            throw new ReadOpException("", this);
        }


        return list.toArray(new ByteBuffer[]{});
    }


    private ByteBuffer getBuffer() {
        return ByteBuffer.allocate(1024);
    }

    public void cancleKey() {
        key.cancel();
    }

    @Override
    public void attach(Object obj) {
        if (key != null) {
            key.attach(obj);
        }
    }

    public SelectionKey getKey() {
        return key;
    }

    public boolean hasWrite() {
        return !towrite.isEmpty();
    }

    public boolean forbidWrite() {
        throw new UnsupportedOperationException("can't implment forbidWrite");
    }

    public boolean forbidRead() {
        throw new UnsupportedOperationException("can't implment forbidRead");
    }

    public void connect() throws IOException {
        this.lifestate.set(OPENED);
    }

    public SelectionKey register(Selector selector, int ops) {
        try {
            channel.configureBlocking(false);
            key = channel.register(selector, ops);
            return key;
        } catch (IOException e) {
            logger.info("can't register");
        }
        return null;
    }

    public int interestOps() {
        return key.interestOps();
    }

    public int interestOps(int ops) {
        return key.interestOps(ops).interestOps();
    }

    public int addOps(int ops) {
        return interestOps(key.interestOps() | ops);
    }

    public int removeOps(int ops) {
        return interestOps(key.interestOps() & (~ops));
    }

    public int read(ByteBuffer dst) throws IOException {
        return channel.read(dst);
    }

    public int write(ByteBuffer src) throws IOException {
        try {
            return channel.write(src);
        } finally {
            try {
                channel.socket().getOutputStream().flush();
            } catch (IOException e) {
                logger.warn("write operation wrong ");
                closeNow();
            }
        }
    }

    public boolean isOpen() {
        return workstate.get() == OPENED;
    }

    public void close() throws IOException {
        closeNow();
    }

    public HttpConnection getConnection() {
        return connection;
    }

    public void setConnection(HttpConnection connection) {
        this.connection = connection;
    }

    public int getLocalPort() {
        return channel.socket().getLocalPort();
    }

    public InetSocketAddress getRemote() {
        return (InetSocketAddress) channel.socket().getRemoteSocketAddress();
    }


}
