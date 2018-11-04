package com.sicnu.cs.servlet.basis;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ServletMap {
    private static ServletMap ourInstance = new ServletMap();

    public static ServletMap getInstance() {
        return ourInstance;
    }

    private ServletMap() {

    }

    private ConcurrentHashMap<String,ServletPosition> pos=new ConcurrentHashMap<>();


    /**
     *
     * @param url serverltUrlPatterns
     * @param position describe servlet info
     */
    public void addUrl(String url,ServletPosition position){
        pos.put(url,position);
    }

    public ServletPosition findServlet(URI uri){
        List<ServletPosition> positions=new ArrayList<>(pos.values());
        return positions.get(0);
    }




}
