package com.cs.sicnu.core.protocol;

import java.net.InetSocketAddress;
import java.util.HashMap;

public class HttpRequest {

    /**
     * 用于描述这个请求属于那个链接
     */


    private String method;
    private String version;
    private String resUrl;
    private long content_length=0;
    private String cookies;
    private String contentType;
    private String boundary;
    private String host;
    private String schema;
    private InetSocketAddress remoteAddress;
    private int localPort;
    private byte data[];

    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    private HashMap<String,String> map=new HashMap<>();

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getResUrl() {
        return resUrl;
    }

    public void setResUrl(String resUrl) {
        this.resUrl = resUrl;
    }

    public long getContent_length() {
        return content_length;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setContent_length(long content_length) {
        this.content_length = content_length;
    }

    public String getCookies() {
        return cookies;
    }

    public void setCookies(String cookies) {
        this.cookies = cookies;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getBoundary() {
        return boundary;
    }

    public void setBoundary(String boundary) {
        this.boundary = boundary;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public String toString() {
        return method+" "+resUrl+" "+version
                +map.toString();
    }

    public void addAttributies(String key,String value){
        map.put(key,value);
    }

    public String getAttributies(String key){
        return map.get(key);
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    public HashMap<String, String> getMap() {
        return map;
    }


    public void setRemoteAddress(InetSocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public int getLocalPort() {
        return localPort;
    }

    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }
}
