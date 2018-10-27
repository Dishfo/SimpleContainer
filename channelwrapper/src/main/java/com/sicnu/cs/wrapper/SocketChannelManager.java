package com.sicnu.cs.wrapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class SocketChannelManager implements ChannelMananger{

    private Logger logger= LogManager.getLogger(getClass().getName());

    private WrappersListener listener=null;
    private ServerSocketChannel serverChannel;
    private Selector selector;

    private CountDownLatch latch=null;
    private ThreadPoolExecutor executor;


    public void init(){
        try {
            serverChannel.configureBlocking(false);
            serverChannel.register(selector,SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SocketChannelManager(ServerSocketChannel serverChannel, Selector selector) {
        this.serverChannel = serverChannel;
        this.selector = selector;
        int num=15;
        executor=new ThreadPoolExecutor(num,num,0, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());
    }

    @Override
    public void processKeys(Set<SelectionKey> keys) {
        resetLatch(keys.size());
        for (SelectionKey key : keys) {
            KeyEvent event = new KeyEvent(key);
            ProcessTask task = new ProcessTask(event);
            executor.execute(new CountDownTask(task, null));
        }

        try {
//            latch.await(10000,TimeUnit.MILLISECONDS);
            latch.await();
        } catch (InterruptedException e) {
            logger.warn("waitting is interrupted");
        }
        logger.debug("all thread run compete");
        keys.clear();
    }

    @Override
    public void setWrapLinstener(WrappersListener listener) {
        this.listener=listener;
    }


    private void processData(ByteBuffer[] data,SelectionKey key){
        logger.debug("use to process data in manager"+data.length+key.toString());
    }

    private void resetLatch(int count){
        if (latch==null){
            latch=new CountDownLatch(count);
        }else if (latch.getCount()==0){
            latch=new CountDownLatch(count);
        }else {
            throw new IllegalStateException("old latch has vaild");
        }
    }

    private final static int ACCEPT_E=0x1;
    private final static int READ_E=0x2;
    private final static int WRITE_E=0X3;

    private class KeyEvent{
        private SelectionKey key;
        private List<Integer> events;

        KeyEvent() {
            events=new ArrayList<>();
        }

        KeyEvent(SelectionKey key){
            this();
            this.key=key;
            if (key.isAcceptable()){
                logger.debug("acp");
                events.add(ACCEPT_E);
            }
            if (key.isWritable()){
                logger.debug("writing");
                events.add(WRITE_E);
            }
            if (key.isReadable()){
                logger.debug("reading");
                events.add(READ_E);
            }
        }

        Integer[] getEvents(){
            return events.toArray(new Integer[]{});
        }
    }

    private class ProcessTask implements Runnable{
        private KeyEvent event;

        ProcessTask(KeyEvent event) {
            this.event = event;
        }

        @Override
        public void run() {
            Integer[] events=event.getEvents();
            SelectionKey key=event.key;

            for (Integer i:events){
                switch (i){
                    case ACCEPT_E:
                        ServerSocketChannel channel
                                = (ServerSocketChannel) key.channel();
                        SocketChannel channel1;
                        try {
                            channel1 = channel.accept();
                            channel1.socket().setSoLinger(true,1000);
                        } catch (IOException e) {
                            logger.warn("accept failed ");
                            continue;
                        }
                        ChannelWrapper wrapper=
                                new SocketChannelWrapper(channel1);
                        SelectionKey newkey=
                                wrapper.register(selector,SelectionKey.OP_READ);
                        listener.onWrapperCreated(wrapper);
                        newkey.attach(wrapper);
                        break;
                    case WRITE_E:
                        ChannelWrapper wrapper_w= (ChannelWrapper) key.attachment();
                        if (wrapper_w!=null){
                            try {
                                listener.onWrapperWrite(wrapper_w);
                                wrapper_w.write();
                            } catch (ClosedChannelException e) {
                                wrapper_w.closeNow();
                            }
                        }
                        break;
                    case READ_E:
                        ChannelWrapper wrapper_r= (ChannelWrapper) key.attachment();
                        if (wrapper_r!=null){
                            try {
                                ByteBuffer[] data=wrapper_r.read();
                                processData(data,key);
                                if (listener!=null){
                                    listener.onWrapperRead(wrapper_r,data);
                                }
                            } catch (ReadOpException e) {
                                logger.warn(e.getMessage());
                                try { wrapper_r.close(); } catch (IOException ignored) { }
                            }
                        }else {
                            key.cancel();
                        }
                        break;
                }
            }
        }
    }

    private class CountDownTask extends FutureTask<Object>{

        CountDownTask(Runnable runnable, Object result) {
            super(runnable, result);
        }

        @Override
        public void run() {
            try {
                super.run();
            }finally {
                latch.countDown();
            }
        }
    }

    public void stop(){
        executor.shutdown();
        try {
            serverChannel.close();
            selector.close();
        } catch (IOException ignored) {}
    }
}
