package com.cs.sicnu.core.protocol;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {


    private HashMap<String,String> attributies;
    private ByteBuffer data=null;
    private String msg;
    private int status;
    private String version;
    private Connection connection;
    private long len;

    public long getLen() {
        return len;
    }

    public void setLen(long len) {
        this.len = len;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /**
     * 把响应与请求进行关联
     * 两则属于同一链接
     * @param request
     */
    public void bindRequest(HttpRequest request){
        setConnection(request.getConnection());
    }

    protected HttpResponse(){}

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void addAttributies(String name, String value){
        attributies.put(name,value);
    }

    public String getAttributies(String name,String value){
        return attributies.get(name);
    }


    public ByteBuffer getData() {
        return data;
    }

    public void setData(ByteBuffer data) {
        this.data = data;
    }

    public HashMap<String, String> getAttributies() {
        return attributies;
    }

    @Override
    public String toString() {
        return "Response: "+attributies.toString();
    }

    public static class Builder{
        private String version;
        private int status;
        private String msg;
        private ByteBuffer data;
        private HashMap<String,String> head=new HashMap<>();

        public Builder setVersion(String version){
            this.version=version;
            return this;
        }

        public String getVersion() {
            return version;
        }

        public int getStatus() {
            return status;
        }

        public String getMsg() {
            return msg;
        }

        public Builder setStatus(int status) {
            this.status = status;
            return this;
        }

        public Builder setMsg(String msg) {
            this.msg = msg;
            return this;
        }

        public Builder setData(ByteBuffer data) {
            this.data = data;
            return this;
        }

        public Builder setHead(String name, String value){
            head.put(name,value);
            return this;
        }

        public Builder setHeaders(HashMap<String,String> headers){
            head=headers;
            return this;
        }

        public Builder addHead(String name,String value){
            String oldval=getHeader(name);
            if (oldval==null){
                setHead(name,value);
            }
            setHead(name,oldval+HttpHeadConstant.HEAD_Separator+value);
            return this;
        }

        public String getHeader(String name){
            return head.get(name);
        }

        public HttpResponse build(){
            HttpResponse response=new HttpResponse();
            response.setVersion(version);
            response.setMsg(msg);
            response.setStatus(status);
            response.setData(data);
            response.attributies=head;
            return response;
        }

    }
}
