package com.sicnu.cs.servlet.container;

import com.sicnu.cs.http.HttpConnection;
import com.sicnu.cs.http.HttpRequest;
import com.sicnu.cs.http.HttpResponse;
import com.sicnu.cs.servlet.basis.ServletMap;
import com.sicnu.cs.servlet.basis.ServletPosition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;

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
        String url=request.getUrl();
        try {
            URI uri=URI.create(url);
            ServletPosition position=servletMap.findServlet(uri);


        }catch (Throwable t){
            response.setStatus(400);
            try {response.outPut();} catch (IOException ignored) {}
        }
    }

    @Override
    protected void processRegistered(String[] url, ServletPosition position) {
        for (String s:url){
            logger.debug(" url == "+s);
        }

        logger.debug(position.getHost()+""+position.getContextPath()+position.getServletMapping());
    }
}
