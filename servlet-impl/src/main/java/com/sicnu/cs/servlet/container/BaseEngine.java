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
import com.sicnu.cs.servlet.basis.map.ServletSearch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;

public class BaseEngine extends RegisterContainer implements Engine {

    private Logger logger = LogManager.getLogger(getClass().getName());
    private ServletMap servletMap = ServletMap.getInstance();

    @Override
    public void handleRequset(HttpConnection connection,
                              HttpRequest request,
                              HttpResponse response) {
        String url = getURI(connection, request);

        URI uri = URI.create(url);
        ServletSearch search = servletMap.findServlet(uri);
        if (!search.isFound()){
            response.setStatus(404);
            try {response.outPut();} catch (IOException ignored) {}
            connection.close();
        }else{
            String conn = response.getHead(HttpHeadConstant.H_CONN);
            Host host=findHost(search.getHost());

            if (availbaleofHost(host)){
                SimpleHost h= (SimpleHost) host;
                HttpPair pair=new HttpPair(connection,response,request);
                h.dispatch(pair,search);
                try {pair.commitResponse();} catch (IOException e) {}
            }else {
                connection.close();
                return;
            }


            if (conn != null && conn.equals(HttpHeadConstant.CONN_CLOSE)) {
                connection.close();
            }
        }
    }

    private Host findHost(InetAddress address) {
        for (Container c : getChilds()) {
            if (c instanceof Host) {
                InetAddress addresses[] = ((Host) c).getInetAddress();
                for (InetAddress a : addresses) {
                    if (a.equals(address)) {
                        return (Host) c;
                    }
                }
            }
        }
        return null;
    }

    @Override
    protected void processRegistered(String[] url, ServletPosition position) {
        for (String s : url) {
            logger.debug(" url == " + s);
        }
        ServletMap map = ServletMap.getInstance();
        for (String s : url) {
            map.addUrl(s, position);
        }
    }

    private String getURI(HttpConnection connection, HttpRequest request) {
        String schema = connection.getSchema();
        String host = request.getHeader(HttpHeadConstant.H_HOST);
        String url = request.getUrl();
        if (StringUtils.isEmpty(schema)
                || StringUtils.isEmpty(host) ||
                StringUtils.isEmpty(url)) {
            throw new IllegalArgumentException(" is not a vaild url ");
        }
        return schema + "://" + host + url;
    }

    private boolean availbaleofHost(Host host){
        if (host==null){
            return false;
        }

        if (host instanceof Container){
            return ((Container) host).getLifeState()==Container.running;
        }
        return true;
    }
}
