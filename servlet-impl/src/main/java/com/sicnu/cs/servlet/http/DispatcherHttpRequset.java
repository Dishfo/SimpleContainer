package com.sicnu.cs.servlet.http;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class DispatcherHttpRequset extends HttpServletRequestWrapper {

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request the {@link HttpServletRequest} to be wrapped.
     * @throws IllegalArgumentException if the request is null
     */
    private DispatcherType dispatcherType;

    public DispatcherHttpRequset(HttpServletRequest request,
                                 DispatcherType dispatcherType) {
        super(request);
        this.dispatcherType=dispatcherType;
    }

    @Override
    public DispatcherType getDispatcherType() {
        return dispatcherType;
    }
}
