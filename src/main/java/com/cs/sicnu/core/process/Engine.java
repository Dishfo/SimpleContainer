package com.cs.sicnu.core.process;

import com.cs.sicnu.core.protocol.Connection;
import com.cs.sicnu.core.protocol.HttpRequest;

/**
 * 请求的分发者
 * 对于一个域名下的请求进行分发，把每个请求分发到
 * 对应的处理者上
 *
 */
public interface Engine {
    void handle(Connection connection,HttpRequest request);
}
