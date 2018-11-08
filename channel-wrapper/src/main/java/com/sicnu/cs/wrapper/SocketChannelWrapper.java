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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * un-thread-safe
 */
public class SocketChannelWrapper implements ChannelWrapper{

    private Logger logger = LogManager.getLogger(getClass().getName());

    private AtomicInteger lifestate;
    private SocketChannel channel;
    private SelectionKey key;
    private HashMap<String,Object> attributies;



    private LinkedBlockingQueue<ByteBuffer> towrite;
    private ByteBuffer posion;

    SocketChannelWrapper(SocketChannel channel) {
        this.channel = channel;
        if (!channel.isOpen()||!channel.isConnected()){
            throw new IllegalArgumentException("the channel is invalid state");
        }
        attributies=new HashMap<>();
        lifestate=new AtomicInteger(OPENED);
        towrite = new LinkedBlockingQueue<>();
        posion=ByteBuffer.allocate(44);
    }



    public void closeNow() {
        if (!lifestate.compareAndSet(OPENED,CLOSED)){
            return;
        }

        key.cancel();
        try {
            channel.close();
        } catch (IOException ignored) {}

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


    public void write() throws WriteOpException {
        ByteBuffer buffer;
        do {
            buffer=towrite.poll();

            if (buffer!=null){
                try {
                    channel.write(buffer);
                } catch (IOException e) {
                    if (e instanceof ClosedChannelException){
                        throw new WriteOpException("",this,WriteOpException.CLOSE_CONN);
                    }else {
                        throw new WriteOpException("",this,WriteOpException.IO_ERROR);
                    }
                }
            }
        }while (buffer!=null);
        removeOps(SelectionKey.OP_WRITE);
    }

    public ByteBuffer[] read() throws ReadOpException {
        List<ByteBuffer> list = new ArrayList<>();
        int len;
        ByteBuffer buffer = getBuffer();
        try {
            while ((len = channel.read(buffer)) > 0) {
                list.add(buffer);
                buffer.flip();
                buffer = getBuffer();
            }
            if (len == -1) {
                throw new ReadOpException("", this,ReadOpException.PEERCLOSE);
            }else if (list.size()==0){
                throw new ReadOpException("", this,ReadOpException.NO_DATA);
            }
        } catch (IOException e) {
            if (e instanceof ReadOpException)
                throw (ReadOpException) e;
            throw new ReadOpException("",this,ReadOpException.UN_KNOW);
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

    @Override
    public InetSocketAddress getRemote() {
        try {
            return (InetSocketAddress)
                    channel.getRemoteAddress();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public InetSocketAddress getHost() {
        try {
            return (InetSocketAddress)
                    channel.getLocalAddress();
        } catch (IOException e) {
            return null;
        }
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

    @Override
    public void setAttributes(String name, Object val) {
        attributies.put(name,val);
    }

    @Override
    public Object getAttribute(String name) {
        return attributies.get(name);
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
        return lifestate.get()==OPENED;
    }



    public void close(){
        if (!lifestate.compareAndSet(OPENED,CLOSING)){
            return;
        }

        removeOps(SelectionKey.OP_READ);
        towrite.add(posion);
        ByteBuffer buffer=null;
        while (buffer!=posion){
            buffer=towrite.poll();
            try {
                write(buffer);
            } catch (IOException e) {
                break;
            }
        }
        key.cancel();
        try {
            channel.close();
        } catch (IOException ignored) {}
        lifestate.set(CLOSED);
    }
}
