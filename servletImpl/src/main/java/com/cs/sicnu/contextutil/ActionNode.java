package com.cs.sicnu.contextutil;

import com.cs.sicnu.http.HttpMessageMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ActionNode {

    void act(HttpServletRequest request,
             HttpServletResponse response,
             HttpMessageMap map, Throwable throwable);


}
