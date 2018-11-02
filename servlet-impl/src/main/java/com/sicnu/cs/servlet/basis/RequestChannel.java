package com.sicnu.cs.servlet.basis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * 一对 httpServletRequset httpServletRespnse
 * 通过context 持有的通道完成事务逻辑
 *
 */
public interface RequestChannel {

    void through(HttpServletRequest request,
                 HttpServletResponse response);
}
