package com.sicnu.cs.servlet.basis.map;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public abstract class Node <T extends Node,P extends Node>{
    protected P p;
    protected HashSet<T> childs=new HashSet<>();
    public P getParent() {
        return p;
    }

    public T addChild(T child){
        child.setParent(this);
        return childs.add(child)?child:null;
    }

    public List<T> getChildren(){
        return new ArrayList<>(childs);
    }

    public void setParent(P parent){
        this.p=parent;
    }
}
