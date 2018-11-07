package com.sicnu.cs.servlet.container;

import com.cs.sicnu.core.process.Container;
import com.cs.sicnu.core.protocol.HttpHeadConstant;
import com.cs.sicnu.core.utils.StringUtils;
import com.sicnu.cs.http.HttpConnection;
import com.sicnu.cs.http.HttpRequest;
import com.sicnu.cs.http.HttpResponse;
import com.sicnu.cs.servlet.basis.HttpPair;
import com.sicnu.cs.servlet.basis.ServletMap;
import com.sicnu.cs.servlet.basis.ServletPosition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.util.List;

public class BaseEngine extends RegisterContainer implements Engine {

    private Logger logger= LogManager.getLogger(getClass().getName());
    private ServletMap servletMap=ServletMap.getInstance();

    @Override
    protected void initInteral() {
        
    }

    @Override
    public void handleRequset(HttpConnection connection,
                              HttpRequest request,
                              HttpResponse response) {
        String url=getURI(connection,request);
        try {
            URI uri=URI.create(url);
            ServletPosition position=servletMap.findServlet(uri);
            if (position==null){
                response.setStatus(404);
                response.outPut();
                return;
            }
            HttpPair pair=new HttpPair(connection,response,request);
            List<InetAddress> addressList=position.getHost();
            if (addressList.size()>0){
                Host h=findHost(addressList.get(0));
                if (h==null){

                }else {
                    h.handleHttp(pair,position);
                }
            }else {
                response.setStatus(404);
            }
        }catch (Throwable t){
            response.setStatus(400);
            try {response.outPut();} catch (IOException ignored) {}
        }

        String conn=response.getHead(HttpHeadConstant.H_CONN);

        if (conn!=null&&conn.equals(HttpHeadConstant.CONN_CLOSE)){
            connection.close();
        }
    }

    private Host findHost(InetAddress address){
        for (Container c:getChilds()){
            if (c instanceof Host){
                InetAddress addresses[]=((Host) c).getInetAddress();
                for (InetAddress a:addresses){
                    if (a.equals(address)){
                        return (Host) c;
                    }
                }
            }
        }
        return null;
    }

    @Override
    protected void processRegistered(String[] url, ServletPosition position) {
        for (String s:url){
            logger.debug(" url == "+s);
        }
        ServletMap map=ServletMap.getInstance();
        for (String s:url){
            map.addUrl(s,position);
        }
    }

    private String getURI(HttpConnection connection,HttpRequest request){
        String schema=connection.getSchema();
        String host=request.getHeader(HttpHeadConstant.H_HOST);
        String url=request.getUrl();
        if (StringUtils.isEmpty(schema)
                ||StringUtils.isEmpty(host)||
        StringUtils.isEmpty(url)){
            throw new IllegalArgumentException(" is not a vaild url ");
        }
        return schema+"://"+host+url;
    }
}
