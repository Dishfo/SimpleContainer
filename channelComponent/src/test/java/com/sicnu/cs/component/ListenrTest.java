package com.sicnu.cs.component;

import com.cs.sicnu.core.protocol.HttpHeadConstant;
import com.sicnu.cs.http.HttpConnection;
import com.sicnu.cs.wrapper.ChannelWrapper;
import com.sicnu.cs.wrapper.ReadOpException;
import com.sicnu.cs.wrapper.WrappersListener;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ListenrTest {

    @Test
    public void test(){
        MessageListener listener=new MessageListener(8080);
        listener.setChannelListener(new TestWrapperListener());
        listener.startListen();

    }


    private class TestWrapperListener implements WrappersListener{

        @Override
        public void onWrapperCreated(ChannelWrapper wrapper) {
            System.out.println("the wrapper create");
            HttpConnection connection=new HttpConnection(wrapper);
            connection.setHandler((connection1, request, response) -> {



                response.setStatus(200);
                response.setHeader(HttpHeadConstant.H_CONT_ENCODING,
                        HttpHeadConstant.H_CONE_DEFLATE);

                File file=new File("/home/dishfo/logs/info.log");
                byte[] data=new byte[0];
                try {
                    FileInputStream fileInputStream=
                            new FileInputStream(file);
                    data=new byte[fileInputStream.available()];
                    fileInputStream.read(data);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    response.getBodyOutStream().write(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    response.outPut();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
            wrapper.setAttributes("conn",connection);
        }

        @Override
        public void onWrapperRead(ChannelWrapper wrapper, ByteBuffer[] data, Exception e) {
            if (e instanceof ReadOpException){
                if (((ReadOpException) e).getOccur()==ReadOpException.PEERCLOSE){
                    try { wrapper.close(); } catch (IOException ignored) {}
                }
            }else {
                HttpConnection conn= (HttpConnection)
                        wrapper.getAttribute("conn");
                conn.receice(data);
            }
        }

        @Override
        public void onWrapperWrite(ChannelWrapper wrapper, Exception e) {

        }
    }
}
