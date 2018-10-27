package com.sicnu.cs.wrapper;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class ChannelWrapperTest {

//    @Test


    @Test
    public void managerTest(){
        ServerSocketChannel serverSocketChannel=
                null;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(8080));
            Selector selector=Selector.open();
            ChannelMananger mananger=new SocketChannelManager(serverSocketChannel,selector);
            ((SocketChannelManager) mananger).init();
            while (selector.select()>0){
                mananger.processKeys(selector.selectedKeys());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
