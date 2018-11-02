package com.sicnu.cs.servlet.container;

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

}
