package com.sicnu.cs.http;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ParseTest {

    @Test
    public void test(){
        HttpParser parser=new HttpParser(new HttpParseListener() {
            @Override
            public void onStartParse(HttpRequest request) {
                System.out.println("start parse ");
                System.out.println(request.getMethod()+" "+request.getUrl()+" " +
                        request.getHTTPVersion());
            }

            @Override
            public void onException(Exception e, int state) {
                System.out.println(" a exception occur");
                System.out.println(e.getClass()+" "+state+((ParseException)e).getParseType());
            }

            @Override
            public void onCompete(HttpRequest request) {
                System.out.println("compete");
                System.out.println(request.headers);
                byte[] data=request.getData();
                if (data!=null){
                    System.out.println(data.length);
                    System.out.println(data[data.length-1]);
                }
            }
        });

        ByteBuffer[] reqs=getRequest("/home/dishfo/logs/request--1538556552050");
        for (ByteBuffer b:reqs){
            parser.resolve(b);
        }

        for (ByteBuffer b:reqs){
            parser.resolve(b);
        }

        for (ByteBuffer b:reqs){
            parser.resolve(b);
        }

        for (ByteBuffer b:reqs){
            parser.resolve(b);
        }
         reqs=getRequest("/home/dishfo/logs/request--2015110231");
        for (ByteBuffer b:reqs){
            parser.resolve(b);
        }


        for (ByteBuffer b:reqs){
            parser.resolve(b);
        }


        for (ByteBuffer b:reqs){
            parser.resolve(b);
        }

        reqs=getRequest("/home/dishfo/logs/request--1538556552050");
        for (ByteBuffer b:reqs){
            parser.resolve(b);
        }

    }

    /**
     *
     * @param path disk file
     * @return data byte steam request
     */
    public ByteBuffer[] getRequest(String path){
        File file=new File(path);
        List<ByteBuffer> res=new ArrayList<>();
        try {
            FileInputStream fin=new FileInputStream(file);
            byte[] bytes=new byte[1024];
            int len=0;
            while ((len=fin.read(bytes))>0){
                ByteBuffer buffer=ByteBuffer.wrap(bytes,0,len);
                res.add(buffer);
                bytes=new byte[1024];
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res.toArray(new ByteBuffer[]{});
    }

}
