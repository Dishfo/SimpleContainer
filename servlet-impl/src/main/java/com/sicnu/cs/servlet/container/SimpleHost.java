package com.sicnu.cs.servlet.container;

import com.cs.sicnu.core.process.Container;
import com.sicnu.cs.servlet.basis.HttpPair;
import com.sicnu.cs.servlet.basis.ServletPosition;
import com.sicnu.cs.servlet.basis.map.ServletSearch;
import com.sicnu.cs.servlet.init.ClassesTransfer;

import javax.servlet.ServletContext;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SimpleHost extends DispatchContainer implements Host{

    private List<InetAddress> vaildAddress;
    private HashMap<String,ServletContext> contextHashMap;
    private ClassesTransfer transfer;

    public SimpleHost() {
        transfer=new ClassesTransfer();
        contextHashMap=new HashMap<>();
        vaildAddress=new ArrayList<>();
    }

    @Override
    public InetAddress[] getInetAddress() {
        return vaildAddress.toArray(new InetAddress[]{});
    }

    @Override
    public void addAddress(InetAddress address) {
        vaildAddress.add(address);
    }

    @Override
    public ServletContext findContext(String contextpath) {
        return contextHashMap.get(contextpath);
    }

    @Override
    public void addContext(ServletContext context) {
        contextHashMap.putIfAbsent(context.getContextPath(),context);
    }

    @Override
    protected void startInteral() {
        contextHashMap.forEach((s, servletContext) -> {
            transfer.findClass((SimpleContext) servletContext);
        });
    }

    @Override
    protected void dispatch(HttpPair pair, ServletSearch search) {
        ServletContext context=findContext(search.getContextPath());
        if (context!=null){
            BaseContext bctx= (BaseContext) context;
            bctx.dispatch(pair,search);
        }else {
            pair.setStatus(503);
        }
    }

    @Override
    public void setParent(Container container) {
        if (!(container instanceof Engine)){
            throw new IllegalArgumentException("host parent must be engine");
        }
        super.setParent(container);
    }

    @Override
    public void addChild(Container container) {
        super.addChild(container);
        if (container instanceof ServletContext){
            ServletContext ctx= (ServletContext) container;
            addContext(ctx);
        }
    }

    @Override
    protected ServletPosition fillPosition(ServletPosition servletPosition) {
        servletPosition.setHost(vaildAddress);
        return servletPosition;
    }
}
