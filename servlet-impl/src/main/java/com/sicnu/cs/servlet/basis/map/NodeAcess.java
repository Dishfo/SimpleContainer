package com.sicnu.cs.servlet.basis.map;

import com.sicnu.cs.servlet.basis.ServletPosition;

import java.net.URL;

public interface NodeAcess {

    ServletSearch find(RootNode node, URL url);
    ServletSearch find(HostNode node, URL url);
    ServletSearch find(ContextNode node, URL url);
    ServletSearch find(ServletNode node, URL url);

    ServletPosition add(RootNode node,ServletPosition position,String url);
    ServletPosition add(HostNode node, ServletPosition position, String url);
    ServletPosition add(ContextNode node,ServletPosition position,String url);
    ServletPosition add(ServletNode node,ServletPosition position,String url);

    boolean match(RootNode node, URL url);
    boolean match(HostNode node, URL url);
    boolean match(ContextNode node, URL url);
    boolean match(ServletNode node, URL url);

}
