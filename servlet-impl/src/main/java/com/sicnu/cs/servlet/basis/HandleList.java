package com.sicnu.cs.servlet.basis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface HandleList {
    void exception(HttpServletRequest req, HttpServletResponse resp,Throwable throwable);
    void nextNode(HttpServletRequest req,HttpServletResponse resp);
    void end(HttpServletRequest req, HttpServletResponse resp);
}
