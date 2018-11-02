package com.sicnu.cs.servlet.http;

import com.sicnu.cs.servlet.basis.RequestChannel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * 暂时用于调用filter chain
 *
 */
public class InteralRequsetChannel implements RequestChannel {


    @Override
    public void through(HttpServletRequest request, HttpServletResponse response) {

    }

    @Override
    public void refreshChannel() {

    }
}
