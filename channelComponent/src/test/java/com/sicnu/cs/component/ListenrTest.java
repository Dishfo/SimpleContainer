package com.sicnu.cs.component;

import org.junit.Test;

public class ListenrTest {

    @Test
    public void test(){
        TcpListener listener=new TcpListener(8080);
        EngineManager.getInstance().addEngine("localhost",new NormalEngine());
        listener.start();
    }
}
