package com.sicnu.cs.servlet.container;

import com.sicnu.cs.http.HttpRequestHandler;

import javax.servlet.ServletContext;
import java.net.InetAddress;

/**
 *
 * 描述一个生效的命名集合
 * 包含一个inetaddress 数组
 *
 *
 */
public interface Host {
    InetAddress[] getInetAddress();

    void addAddress(InetAddress address);
    ServletContext findContext(String uripath);
    void addContext(ServletContext context);

}
