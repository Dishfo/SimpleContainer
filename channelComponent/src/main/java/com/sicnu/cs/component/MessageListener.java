package com.sicnu.cs.component;

import com.sicnu.cs.wrapper.ChannelMananger;
import com.sicnu.cs.wrapper.SocketChannelManager;
import com.sicnu.cs.wrapper.WrappersListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 用与监听接口
 */
public class MessageListener {

    private ChannelMananger mananger;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;

    private int port;

    private int cmdPort = 8005;

    private AtomicBoolean listened;

    public MessageListener(int port) {
        listened = new AtomicBoolean(false);
        this.port = port;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(port));
            selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mananger = new SocketChannelManager(serverSocketChannel, selector);
        mananger.init();
    }

    public void startListen() {

        if (listened.compareAndSet(false, true)) {
            new Thread(new ListenTask())
                    .start();
            waitEndCmd();
        }
    }

    private static final String STOP_CMD="stopnow";
    private void waitEndCmd() {

        try {
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress("127.0.0.1",cmdPort), 1);
            while (listened.get()){
                Socket socket=serverSocket.accept();
                InputStream inputStream=
                            socket.getInputStream();
                byte[] b=new byte[30];
                int len;
                if ((len=inputStream.read(b))>0){
                    String s=new String(b,0,len);
                    if (s.startsWith(STOP_CMD)){
                        stop();
                        return;
                    }
                }
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setChannelListener(WrappersListener wrappersListener){
        mananger.setWrapLinstener(wrappersListener);
    }

     private void stop() {
        listened.set(false);
        mananger.stop();
        try {
            selector.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            serverSocketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ListenTask implements Runnable {

        @Override
        public void run() {
            try {
                while (listened.get()) {
                    if (selector.select() <= 0) {
                        continue;
                    }
                    Set<SelectionKey> keys = selector.selectedKeys();
                    mananger.processKeys(keys);
                }
            } catch (IOException e) {
                stop();
            }
        }
    }


}
