package com.sicnu.cs.servlet.basis.map;

public class ContextNode extends Node<ServletNode,HostNode> {
    private String contextPath;

    public ContextNode(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getContextPath() {
        return contextPath;
    }
}







