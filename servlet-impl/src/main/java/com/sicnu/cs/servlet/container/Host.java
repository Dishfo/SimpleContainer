package com.sicnu.cs.servlet.container;

import com.sicnu.cs.servlet.basis.ServletPosition;
import com.sicnu.cs.servlet.basis.HttpPair;

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
    void handleHttp(HttpPair pair, ServletPosition position);
}
