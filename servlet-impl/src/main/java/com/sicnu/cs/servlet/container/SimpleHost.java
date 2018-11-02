package com.sicnu.cs.servlet.container;

import com.cs.sicnu.core.process.BaseContainer;
import com.cs.sicnu.core.process.Container;

import javax.servlet.ServletContext;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class SimpleHost extends BaseContainer implements Host{

    private List<InetAddress> vaildAddress;

    public SimpleHost() {
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
        for (Container c:getChilds()){
            if (c instanceof ServletContext){
                ServletContext context= (ServletContext) c;
                if (uripath.startsWith(context.getContextPath())){
                    return context;
                }
            }
        }
        return null;
    }
}
