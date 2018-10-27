package com.cs.sicnu.core.protocol;

import java.net.InetSocketAddress;

/**
 *描述一条可用的tcp链接
 */
public abstract class Connection {

    public static final String HTTP_SCHEMA="http";
    public static final String HTTPS_SCHEMA="https";

    private InetSocketAddress remote;
    private int loadPort;

    private String schema;

    public InetSocketAddress getRemote() {
        return remote;
    }

    public void setRemote(InetSocketAddress remote) {
        this.remote = remote;
    }

    public int getLoadPort() {
        return loadPort;
    }

    public void setLoadPort(int loadPort) {
        this.loadPort = loadPort;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public abstract void close();
}
