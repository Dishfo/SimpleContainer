package com.sicnu.cs.servlet.basis.map;

import com.sicnu.cs.servlet.basis.ServletPosition;

import javax.servlet.http.MappingMatch;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

public class NodeAcessImpl  implements NodeAcess {

    @Override
    public ServletSearch find(RootNode node, URL url) {
        List<HostNode> hosts=node.getChildren();
        ServletSearch servletSearch=null;
        for (HostNode host:hosts){

            if (match(host,url)){
                servletSearch=find(host,url);
            }

            if (servletSearch!=null
                    &&servletSearch.isFound()){
                break;
            }
        }

        return servletSearch!=null?servletSearch:new ServletSearch(false);
    }

    @Override
    public ServletSearch find(HostNode node, URL url) {
        List<ContextNode> contexts=node.getChildren();
        ServletSearch servletSearch=null;

        for (ContextNode ctx:contexts){

            if (match(ctx,url)){
                servletSearch=find(ctx,url);
            }
            if (servletSearch!=null
                    &&servletSearch.isFound()){
                try {
                    servletSearch.setHost(InetAddress.getByName(url.getHost()));
                } catch (UnknownHostException e) {continue;}
                break;
            }
        }

        return servletSearch!=null?servletSearch:new ServletSearch(false);
    }

    @Override
    public ServletSearch find(ContextNode node, URL url) {
        List<ServletNode> servlets=node.getChildren();
        ServletSearch servletSearch=null;

        for (ServletNode svlet:servlets){
            servletSearch=find(svlet,url);
            if (servletSearch.isFound()){
                servletSearch.setContextPath(node.getContextPath());
                break;
            }
        }

        return servletSearch!=null?servletSearch:new ServletSearch(false);
    }

    @Override
    public ServletSearch find(ServletNode node, URL url) {
        ServletSearch servletSearch=null;
        List<String> urlp=node.getUrlPatterns();
        String path=url.getPath();
        path=path.substring(node.getParent().getContextPath().length());
        for (String p:urlp){
            servletSearch=urlMatch(p,path);
            if (servletSearch.isFound()){
                servletSearch.setServletName(node.getName());
                break;
            }
        }

        return servletSearch!=null?servletSearch:new ServletSearch(false);
    }

    private ServletSearch urlMatch(String urlpattern,String url){
        ServletSearch search=null;
        if (url.equals(urlpattern)){
            search=new ServletSearch(true);
            search.setMappingMatch(MappingMatch.EXACT);
            search.setMatchValue(url);
        }
        return search!=null?search:new ServletSearch(false);
    }


    @Override
    public ServletPosition add(RootNode node, ServletPosition position, String url) {
        List<HostNode> hosts=node.getChildren();

        for (HostNode host:hosts){
            List<InetAddress> ads=position.getHost();
            for (InetAddress address: ads){
                if (match(host,address.getHostName())){
                    return add(host,position,url);
                }
            }
        }

        HostNode newhost=new HostNode(position.getHost());
        newhost=node.addChild(newhost);
        if (newhost!=null){
            return add(newhost,position,url);
        }

        return null;
    }

    @Override
    public ServletPosition add(HostNode node, ServletPosition position, String url) {
        List<ContextNode> contextNodes=node.getChildren();
        for (ContextNode ctx:contextNodes){
            if (ctx.getContextPath().equals(position.getContextPath())){
                return add(ctx,position,url);
            }
        }

        ContextNode newctx=new ContextNode(position.getContextPath());
        newctx=node.addChild(newctx);
        if (newctx!=null){
            return add(newctx,position,url);
        }

        return null;
    }

    @Override
    public ServletPosition add(ContextNode node, ServletPosition position, String url) {
        List<ServletNode> servletNodes=node.getChildren();
        for (ServletNode servlet:servletNodes){
            if (servlet.getName().equals(position.getServletName())){
                return add(servlet,position,url);
            }
        }
        ServletNode newservlet=new ServletNode(position.getServletName());
        newservlet.addUrlPattern(url);
        newservlet=node.addChild(newservlet);
        if (newservlet!=null){
            return position;
        }
        return null;
    }

    @Override
    public ServletPosition add(ServletNode node, ServletPosition position, String url) {
        List<String> urlpatterns=node.getUrlPatterns();
        for (String s:urlpatterns){
            if (s.equals(url)) {
                return null;
            }
        }
        node.addUrlPattern(url);
        return position;
    }

    @Override
    public boolean match(RootNode node, URL url) {
        return true;
    }


    @Override
    public boolean match(HostNode node, URL url) {
        return match(node,url.getHost());
    }

    private boolean match(HostNode node,String host){
        List<InetAddress> addresses=node.getAddresses();
        for (InetAddress address:addresses){
            if (address.getHostName().equals(host)||
                    address.getHostAddress().equals(host)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean match(ContextNode node, URL url) {
        String path=url.getPath();
        if (path!=null&&path.startsWith(node.getContextPath())){
            return true;
        }
        return false;
    }

    @Override
    public boolean match(ServletNode node, URL url) {
        return true;
    }
}
