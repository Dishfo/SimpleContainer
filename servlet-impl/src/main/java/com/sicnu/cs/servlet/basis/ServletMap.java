package com.sicnu.cs.servlet.basis;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;

public class ServletMap {
    private static ServletMap ourInstance = new ServletMap();

    public static ServletMap getInstance() {
        return ourInstance;
    }

    private ServletMap() {

    }

    private ConcurrentHashMap<String,ServletPosition> pos=new ConcurrentHashMap<>();



    public void addUrl(String url,ServletPosition position){
        if (pos.putIfAbsent(url,position)!=null){
            throw new IllegalArgumentException(" confilct urls");
        }
    }

    public ServletPosition findServlet(URI uri){

        return null;
    }




}
