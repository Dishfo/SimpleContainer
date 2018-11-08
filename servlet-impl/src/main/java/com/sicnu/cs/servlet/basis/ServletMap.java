package com.sicnu.cs.servlet.basis;

import com.sicnu.cs.servlet.basis.map.NodeAcess;
import com.sicnu.cs.servlet.basis.map.NodeAcessImpl;
import com.sicnu.cs.servlet.basis.map.RootNode;
import com.sicnu.cs.servlet.basis.map.ServletSearch;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ServletMap {
    private static ServletMap ourInstance = new ServletMap();

    public static ServletMap getInstance() {
        return ourInstance;
    }
    private RootNode rootNode;
    private NodeAcess acess;

    private ServletMap() {
        rootNode=new RootNode();
        acess=new NodeAcessImpl();
    }

    /**
     *
     * @param url serverltUrlPatterns
     * @param position describe servlet info
     */
    public void addUrl(String url,ServletPosition position){
        acess.add(rootNode,position,url);
    }

    public ServletSearch findServlet(URI uri){
        if (uri==null){
            return null;
        }
        try {
            ServletSearch servletSearch=acess.find(rootNode,uri.toURL());
            return servletSearch;
        } catch (MalformedURLException e) {
            return null;
        }
    }







}
