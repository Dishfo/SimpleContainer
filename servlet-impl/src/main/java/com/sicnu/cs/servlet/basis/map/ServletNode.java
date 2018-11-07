package com.sicnu.cs.servlet.basis.map;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ServletNode extends Node<Node,ContextNode> {

    private String name;
    private Set<String> urlpatterns;

    public ServletNode(String name) {
        this.name = name;
        urlpatterns=new HashSet<>();
    }

    public void addUrlPattern(String url){
        urlpatterns.add(url);
    }

    public List<String> getUrlPatterns(){
        return new ArrayList<>(urlpatterns);
    }

    public String getName(){
        return name;
    }

    @Override
    public Node addChild(Node child) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Node> getChildren() {
        throw new UnsupportedOperationException();
    }
}





















