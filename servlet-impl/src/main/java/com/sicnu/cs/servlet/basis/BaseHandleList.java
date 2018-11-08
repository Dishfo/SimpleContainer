package com.sicnu.cs.servlet.basis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class BaseHandleList implements HandleList {

    private Logger logger= LogManager.getLogger(getClass().getName());

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
            logger.debug("end process");
            end(req,resp);
        }else {
            HandleNode node=nodes.get(cur++);
            try {
                logger.debug("node");
                node.handle(req,resp);
            }catch (Throwable throwable){
                logger.debug("exception occur");
                exception(req,resp,throwable);
                end(req,resp);
                return;
            }

            if (node.through()){
                logger.debug("next");
                nextNode(req,resp);
            }else {
                logger.debug("through end");
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
