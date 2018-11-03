package com.sicnu.cs.servlet.container;

import com.cs.sicnu.core.process.Container;
import com.sicnu.cs.servlet.basis.ServletPosition;

import javax.servlet.ServletContext;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SimpleHost extends RegisterContainer implements Host{

    private List<InetAddress> vaildAddress;
    private HashMap<String,ServletContext> contextHashMap;

    public SimpleHost() {
        contextHashMap=new HashMap<>();
        vaildAddress=new ArrayList<>();
    }

    @Override
    protected void initInteral() {

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
    public ServletContext findContext(String uripath) {
        List<ServletContext> result=new ArrayList<>();
        contextHashMap.forEach((s, context) -> {
            if (uripath.startsWith(s)){
                result.add(context);
            }
        });

        return result.size()==0?null:result.get(0);
    }

    @Override
    public void addContext(ServletContext context) {
        contextHashMap.putIfAbsent(context.getContextPath(),context);
    }

    @Override
    public void setParent(Container container) {
        if (!(container instanceof Engine)){
            throw new IllegalArgumentException("host parent must be engine");
        }
        super.setParent(container);
    }

    @Override
    protected ServletPosition fillPosition(ServletPosition servletPosition) {
        servletPosition.setHost(vaildAddress);
        return servletPosition;
    }
}
