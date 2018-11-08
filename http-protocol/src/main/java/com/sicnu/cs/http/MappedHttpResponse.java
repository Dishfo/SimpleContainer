package com.sicnu.cs.http;

import com.cs.sicnu.core.protocol.Http11Constant;
import com.cs.sicnu.core.protocol.HttpHeadConstant;
import com.cs.sicnu.core.protocol.StatusConstant;
import com.cs.sicnu.core.utils.StringUtils;
import com.sicnu.cs.http.encode.EncodingUtil;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class MappedHttpResponse implements HttpResponse {

    private HashMap<String,String> headers=new HashMap<>();
    private ByteArrayOutputStream oriBody;
    private ByteArrayOutputStream respStream;
    private String version;
    private String msg;
    private int status=200;
    private Charset headCharset;
    private AtomicBoolean committed;
    private List<String> cookies;

    MappedHttpResponse(ByteArrayOutputStream respStream) {
        this.respStream = respStream;
        oriBody=new ByteArrayOutputStream(Http11Constant.CONTENT_MAX_SIZE);
        committed=new AtomicBoolean(false);
        headCharset=Charset.forName("UTF-8");
        cookies=new ArrayList<>();
    }

    @Override
    public void setVersion(String version) {
        this.version=version;
    }

    @Override
    public void setHeader(String name, String val) {
        headers.put(name.toLowerCase(),val);
    }

    @Override
    public void addHeader(String name, String val) {
        String lname=name.toLowerCase();
        String old=headers.get(name);
        if (StringUtils.isEmpty(old)){
            headers.put(lname,val);
        }else {
            headers.put(lname,old+ HttpHeadConstant.HEAD_Separator+val);
        }
    }

    @Override
    public void setStatus(int sc) {
        String msg=StatusConstant.getMsg(sc);
        if (msg==null){ throw new IllegalArgumentException("don't support status");}
        this.msg=msg;
        this.status=sc;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getHead(String name) {
        return headers.get(name);
    }

    @Override
    public OutputStream getBodyOutStream() throws IOException {
        if (oriBody.size()>=Http11Constant.CONTENT_MAX_SIZE){
            throw new IOException("to much data");
        }
        return oriBody;
    }

    @Override
    public Writer getBodyWriter() throws IOException {
        if (oriBody.size()>=Http11Constant.CONTENT_MAX_SIZE){
            throw new IOException("to much data");
        }
        return new PrintWriter(oriBody);
    }

    @Override
    public void cleanHead() {
        headers.clear();
    }

    @Override
    public void cleanBody() {
        oriBody.reset();
    }

    @Override
    public boolean isCommitted() {
        return committed.get();
    }

    @Override
    public void setHeadEncoding(String encoding) {
        try {
            headCharset=Charset.forName(encoding);
        }catch (Throwable ignored){}
    }

    @Override
    public void addCookie(String s) {
        if (!StringUtils.isEmpty(s))
            cookies.add(s);
    }


    @Override
    public void outPut() throws IOException {
        if (committed.compareAndSet(false,true)){

            msg=StatusConstant.getMsg(status);

            Objects.requireNonNull(msg);
            Objects.requireNonNull(version);

            byte[] crtl="\r\n".getBytes(headCharset);
            byte[] headsplit=": ".getBytes(headCharset);

            String statusline=version+" "+status+" "+msg;
            respStream.write(statusline.getBytes(headCharset));
            respStream.write(crtl);

            for (Map.Entry<String,String> e:headers.entrySet()){
                respStream.write(e.getKey().getBytes(headCharset));
                respStream.write(headsplit);
                respStream.write(e.getValue().getBytes(headCharset));
                respStream.write(crtl);
            }

            //set-cookie 写入
            for (String cookie:cookies){
                respStream.write(HttpHeadConstant.H_SET_COOKIE.getBytes());
                respStream.write(headsplit);
                respStream.write(cookie.getBytes(headCharset));
                respStream.write(crtl);
            }

            String encoding=headers.get(HttpHeadConstant.H_CONT_ENCODING);
            if (encoding==null){
                encoding=HttpHeadConstant.H_CONE_IDENTITY;
            }

            byte[] body=
                    EncodingUtil.encoding(oriBody.toByteArray()
                            ,encoding);

            setHeader(HttpHeadConstant.H_CONT_LEN,body.length+"");

            if (body.length>Http11Constant.CONTENT_MAX_SIZE){
                throw new IllegalArgumentException(" len of  response body is too long");
            }

            String lenline=HttpHeadConstant.H_CONT_LEN+": "
                    +body.length+"\r\n";

            respStream.write(lenline.getBytes(headCharset));
            respStream.write(crtl);

            respStream.write(body);
            respStream.flush();
        }
    }
}
