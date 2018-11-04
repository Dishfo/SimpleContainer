package com.sicnu.cs.servlet.container;

import com.cs.sicnu.core.process.Container;
import com.sicnu.cs.servlet.basis.ServletPosition;
import com.sicnu.cs.servlet.basis.HttpPair;

import javax.servlet.ServletContext;
import java.io.IOException;
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
        Container[] childern=getChilds();

        for (Container c:childern){
            if (c instanceof ServletContext){
                if (uripath.equals(((ServletContext) c).getContextPath())){
                    result.add((ServletContext) c);
                }
            }
        }

        return result.size()==0?null:result.get(0);
    }

    @Override
    public void addContext(ServletContext context) {
        contextHashMap.putIfAbsent(context.getContextPath(),context);
    }

    @Override
    public void handleHttp(HttpPair pair, ServletPosition position) {
        ServletContext context=findContext(position.getContextPath());
        if (context!=null){
            if (context instanceof BaseContext){
                ((BaseContext) context).process(pair,position);
            }
        }else {
            pair.setStatus(404);
            try {pair.commitResponse();} catch (IOException ignored) {}
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
    protected ServletPosition fillPosition(ServletPosition servletPosition) {
        servletPosition.setHost(vaildAddress);
        return servletPosition;
    }
}
