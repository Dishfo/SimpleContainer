package com.sicnu.cs.component;

import com.sicnu.cs.wrapper.ChannelMananger;
import com.sicnu.cs.wrapper.SocketChannelManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class TcpListener  {
    private static final String CLOSE_CMD = "shutdown";

    private Logger logger= LogManager.getLogger(TcpListener.class.getName());

    private Selector selector = null;
    private ServerSocketChannel serverChannel = null;
    private int port = -1;

    private int commandPort = 8005;

    private Listener listener = null;
    private Thread listenerThread = null;
    private ChannelMananger mananger;


    public TcpListener(int port) {
        this.port = port;
    }

    public void init() {
        try {
            if (selector == null)
                selector = Selector.open();
            if (serverChannel == null) {
                serverChannel = ServerSocketChannel.open();
                serverChannel.bind(new InetSocketAddress(port));
                serverChannel.configureBlocking(false);
            }

            listener = new Listener(selector, serverChannel);
            listenerThread=new Thread(listener);

            mananger=new SocketChannelManager(serverChannel,selector);
            mananger.setWrapLinstener(new HttpWrappersListener());
            ((SocketChannelManager) mananger).init();

        } catch (IOException e) {
            logger.warn("init failed");
        }

        EngineManager engineManager=EngineManager.getInstance();

        engineManager.startAllEngine();
    }

    public void await() {
        try {
            ServerSocket serverSocket = new ServerSocket(commandPort,1);


            ByteBuffer buffer = ByteBuffer.allocate(30);
            while (true) {
                Socket socket = serverSocket.accept();
                InputStream in=socket.getInputStream();
                BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                String s=reader.readLine();

                if (s!=null&&s.startsWith(CLOSE_CMD)){
                    break;
                }

            }
            ((SocketChannelManager)mananger).stop();
            serverSocket.close();

        } catch (IOException e) {
            logger.warn("main thread active failed,the thread will exit");
        }
        logger.info("the tcp listener will be stop");
    }

    public void start() {
        logger.info("starting");
        init();
        try {
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (ClosedChannelException e) {
            logger.warn("server channel register failed");
            return;
        }
        listenerThread.start();
        await();
    }

    public Selector getSelector() {
        return selector;
    }

    public ServerSocketChannel getServerChannel() {
        return serverChannel;
    }

    public int getPort() {
        return port;
    }

    private volatile boolean running=true;
    class Listener implements Runnable {
        Selector selector;
        ServerSocketChannel serverChannel;


        public Listener(Selector selector, ServerSocketChannel channel) {
            this.selector = selector;
            this.serverChannel = channel;
        }

        @Override
        public void run() {
            try {
                while (selector.select()>0||running) {
                    mananger.processKeys(selector.selectedKeys());
                }
                logger.debug("end listener");
            } catch (IOException e) {
                logger.warn("the listener go wrong");
            }
        }


    }
}
