package com.cs.sicnu.core.protocol;

import java.net.InetSocketAddress;

/**
 *描述一条可用的tcp链接
 */
public abstract class Connection<I,O> {

    public abstract void receice(I data);
    public abstract void receice(I[] data);


    public abstract void output(O data);
    public abstract void output(O[] data);

    protected String schema;

    public abstract InetSocketAddress getHost();


    public abstract InetSocketAddress getRemote();

    public abstract void close();

    public String  getSchema(){
        return schema;
    }

}
