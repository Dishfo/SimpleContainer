package com.cs.sicnu.http;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpFilter;
import java.io.IOException;
import java.util.Iterator;

public class HttpFilterChainImpl implements FilterChain {

    private Iterator<HttpFilter> filterIterator;

    public HttpFilterChainImpl(Iterator<HttpFilter> filterIterator) {
        this.filterIterator = filterIterator;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        if (filterIterator.hasNext()){
            filterIterator.next().doFilter(request,response,this);
        }
    }






}
