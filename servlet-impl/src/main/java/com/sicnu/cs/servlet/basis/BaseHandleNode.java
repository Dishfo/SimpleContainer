package com.sicnu.cs.servlet.basis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class BaseHandleNode implements BaseHandleList.HandleNode {

    protected boolean through=true;

    @Override
    public boolean through() {
        return through;
    }

    public abstract void refresh();

    @Override
    public abstract void handle(HttpServletRequest req, HttpServletResponse resp) throws Exception;
}
