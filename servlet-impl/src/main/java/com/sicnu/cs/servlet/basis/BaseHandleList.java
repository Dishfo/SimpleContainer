package com.sicnu.cs.servlet.basis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class BaseHandleList implements HandleList {

    protected List<HandleNode> nodes;
    protected int cur=0;

    public BaseHandleList() {
        nodes=new ArrayList<>();
    }

    @Override
    public void exception(HttpServletRequest req, HttpServletResponse resp, Throwable throwable) {}

    @Override
    public void nextNode(HttpServletRequest req, HttpServletResponse resp) {
        if (cur==nodes.size()){
            end(req,resp);
        }else {
            HandleNode node=nodes.get(cur++);
            try {
                node.handle(req,resp);
            }catch (Throwable throwable){
                exception(req,resp,throwable);
                end(req,resp);
                return;
            }

            if (node.through()){
                nextNode(req,resp);
            }else {
                end(req,resp);
            }
        }
    }

    @Override
    public void end(HttpServletRequest req, HttpServletResponse resp) {

    }

    public interface HandleNode{
        boolean through();
        void handle(HttpServletRequest req, HttpServletResponse resp) throws Exception;
    }
}
