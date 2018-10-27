package com.cs.sicnu.http;

import com.cs.sicnu.core.protocol.*;
import com.cs.sicnu.core.utils.BoundOutputStream;
import com.cs.sicnu.core.utils.BytesServletInputStream;
import com.sicnu.cs.wrapper.HttpConnection;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Objects;

/**
 * 用与关联 httpservletrequest 和 httpservletresponse
 * httpconnetion 并且设置context上下文
 * 持有重量级的字段 输入输出缓冲区
 * request 和 response 只是对这个类
 * 核心的访问接口
 *
 */

public class HttpMessageMap {

    private static final int MAX_OUT_SIZE=1024*1024*8;

    private Context context;
    private volatile boolean isvaild;
    private HttpRequest request;
    private HttpResponse response;
    private HttpResponse.Builder builder=new HttpResponse.Builder();
    private HttpConnection connection;
    private URI uri;

    private ByteBuffer inData;
    private ByteBuffer outData;

    private HttpServletMapping mapping;


    /**
     * 对于一次 map 应该只存在一对唯一请求响应
     *
     */
    private HttpServletRequest servletRequest;
    private HttpServletResponse servletResponse;

    public void init(){
        byte[] tmp=request.getData();
        inData=ByteBuffer.wrap(tmp==null?new byte[]{}:tmp);
        outData=ByteBuffer.allocate(MAX_OUT_SIZE);
        RequestInfoAccess requestInfoAccess=new RequestInfoAccess(this);
        servletRequest=new MappedServletRequest(requestInfoAccess);
        servletResponse=new MappedServletResponse(new ResponseInfoAcess(this));
    }

    void init(Context context) throws IOException, ParseException {
        this.context=context;
        Objects.requireNonNull(context);
        isvaild=true;
        ((MappedServletRequest)servletRequest).compete();
    }



    public void setUri(URI uri){
        this.uri=uri;
    }

    HttpServletRequest getHttpServletRequest(){
        return servletRequest;
    }


    HttpServletResponse getHttpServletResponse(){
        return servletResponse;
    }

    public boolean isIsvaild() {
        return isvaild;
    }

    /**
     * 主要是固定状态
     * 把servletresponse 写入空的httpresponse;
     */

    private void commit(){
        connection.outPut(response);
    }

    public void setServletMapping(HttpServletMapping mapping){
        this.mapping=mapping;
    }

    void outPutResponse(){
        Objects.requireNonNull(response);
        connection.outPut(response);
    }

    public static class MessageMapFactory implements HttpMsgMapFactory<HttpMessageMap>{

        @Override
        public HttpMessageMap create(HttpRequest request, HttpConnection connection) {
            HttpMessageMap map=new HttpMessageMap();
            map.connection=connection;
            map.request=request;
            return map;
        }
    }

    /**
     *
     * 面向sevletrequest 的对内部访问接口
     */
    public static class RequestInfoAccess{
        HttpMessageMap map;

        RequestInfoAccess(HttpMessageMap map) {
            this.map = map;
        }

        String getContextPath(){
            return map.context.getContextPath();
        }

        HttpServletMapping getHttpServletMapping(){
            return map.mapping;
        }

        public String getRealPath(String path){
            return map.context.getRealPath(path);
        }

        InetAddress getRemoteAddress(){
            return map.request.getRemoteAddress().getAddress();
        }

        int getLocalPort(){
            return map.request.getLocalPort();
        }

        String getHeader(String name){
            return map.request.getAttributies(name);
        }

        Enumeration<String> getHeaderNames(){
            return new Context.
                    IteratorEnumeration<>(map.request.getMap()
            .keySet().iterator());
        }

        ServletContext getServletContext(){
            return map.context;
        }

        String getHostName(){
            return map.request.getHost();
        }

        String getMethod(){
            return map.request.getMethod();
        }

        public String getPath(){
            return map.uri.getPath();
        }

        public String getProtocol(){
            return map.request.getVersion();
        }

        String getSchema(){
            return map.request.getSchema();
        }

        ByteBuffer getData(){
            return map.inData;
        }

        URI getUri(){
            return map.uri;
        }

        ServletInputStream getInputStream(){
            return new BytesServletInputStream(map.inData.array());
        }

    }

    /**
     *
     * 对 context访问接口
     * 持有构造中的response
     *  以及outputStream
     */

    static class ResponseInfoAcess {
        private HttpMessageMap map;
        private HttpResponse.Builder builder;
        private BoundOutputStream outputStream=null;
        private HashMap<String,String> headers;
        private PrintWriter writer;

        ResponseInfoAcess(HttpMessageMap map) {
            this.map=map;
            headers=new HashMap<>();
            builder=new HttpResponse.Builder();
            outputStream=new BoundOutputStream(map.outData);
            writer=new PrintWriter(outputStream);
            builder.setVersion(map.request.getVersion());
            setState(HttpServletResponse.SC_OK);
            setHeader(HttpHeadConstant.H_CONT_LEN,0+"");
        }

        void setHeader(String name, String value){
            headers.put(name,value);
        }

        void addHead(String name, String value){
            String oldval=getHeader(name);
            if (oldval==null){
                setHeader(name,value);
            }else{
                setHeader(name,oldval+ HttpHeadConstant.HEAD_Separator+value);
            }
        }

        void setState(int sc){
            String msg= StatusConstant.getMsg(sc);
            builder.setStatus(sc);
            builder.setMsg(msg==null?"...":msg);
        }

        ServletOutputStream getOutputStream(){
            return outputStream;
        }

        PrintWriter getWriter(){
            return writer;
        }
        void commit(){
            writer.flush();
            builder.setData(map.outData)
                    .setHeaders(headers);
            builder.setHead(HttpHeadConstant.H_CONT_LEN,outputStream.getCount()+"");
            map.response=builder.build();
            map.response.setLen(outputStream.getCount());
            map.commit();
        }

        String getHeader(String name){
            return headers.get(name);
        }

        int getStatus(){
            return builder.getStatus();
        }

        Collection<String> getHeaderNames(){
            return headers.keySet();
        }

        String getCharacterEncoding(){
            return map.context.getResponseCharacterEncoding();
        }

        public String HostName(){
            return map.request.getHost();
        }

        void clearData(){

        }

        void clearHears(){

        }


    }

}
