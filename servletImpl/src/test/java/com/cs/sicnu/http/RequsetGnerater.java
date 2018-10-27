package com.cs.sicnu.http;

import com.cs.sicnu.core.process.Poster;
import com.cs.sicnu.core.protocol.Http11Parser;
import com.cs.sicnu.core.protocol.HttpRequest;
import com.cs.sicnu.core.protocol.ParseException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 *
 * 构造一个用于测试用的 HttpRequset
 *  数据来源是我本机的文件
 */
public class RequsetGnerater {
    static HttpRequest create(String filepath){
        Http11Parser parser=new Http11Parser();
        SyncPoster poster=new SyncPoster();
        parser.setPoster(poster);
        File file=new File(filepath);
        try {
            FileInputStream inputStream=
                    new FileInputStream(file);

            byte buf[]=new byte[1024];
            int len=0;
            try {
                while ((len=inputStream.read(buf))>0){
                    try {
                        parser.resolve(ByteBuffer.wrap(buf,0,len));
                        buf=new byte[1024];
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return poster.get();
    }

    static class SyncPoster implements Poster<HttpRequest>{
        HttpRequest request=null;

        @Override
        public void post(HttpRequest request) {
            this.request=request;
        }

        public HttpRequest get(){
            return request;
        }
    }

}
