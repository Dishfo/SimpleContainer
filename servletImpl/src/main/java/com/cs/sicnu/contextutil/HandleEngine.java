package com.cs.sicnu.contextutil;

import com.cs.sicnu.core.process.Bundle;
import com.cs.sicnu.core.process.Engine;
import com.cs.sicnu.core.process.Port;
import com.cs.sicnu.core.protocol.Connection;
import com.cs.sicnu.core.protocol.HttpRequest;
import com.cs.sicnu.core.protocol.HttpResponse;
import com.cs.sicnu.http.HttpMessageMap;
import com.sicnu.cs.wrapper.HttpConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletMapping;
import java.net.URI;

public class HandleEngine extends PortContainer implements Engine {
    private Logger logger= LogManager.getLogger(getClass().getName());

    private HttpMessageMap.MessageMapFactory factory=new HttpMessageMap.MessageMapFactory();

    @Override
    protected void initInteral() {
        super.initInteral();
        addChild(new Host("localhost"));
    }

    @Override
    protected void startInteral() {

        super.startInteral();
    }


    public static final String REQUEST="request";

    @Override
    public void handle(Connection connection, HttpRequest request) {
        String uri = request.getSchema() +
                "://" +
                request.getHost() +
                request.getResUrl();
        URI uri1=URI.create(uri);

        HttpMessageMap map=factory.create(request, (HttpConnection) connection);



        Bundle bundle=
                PortMap.getInstance().findServlet(request.getHost(),
                        uri1.getPath());

        Port port= (Port) bundle.getData(PortMap.DST_PORT);
        if (port!=null){
            HttpServletMapping mapping= (HttpServletMapping) bundle.getData(PortMap.SERVLET_MAPPING);
            map.setServletMapping(mapping);
            map.setUri(uri1);
            map.init();
            bundle.putData(REQUEST,map);
            port.process(bundle);
        }else {
            logger.debug("the request is contextroot or default");

        }
    }

    @Override
    public void process(Object o) {
        if (o instanceof HttpResponse){
            HttpResponse response= (HttpResponse) o;
            logger.debug("engine get a response");
            if (response.getConnection() instanceof HttpConnection){
                HttpConnection connection=
                        (HttpConnection) response.getConnection();
                connection.outPut(response);
            }
        }
    }

    @Override
    protected void registerFeature(Feature feature) {
        if (feature instanceof PortFeature){
            PortMap.getInstance().registerPort((PortFeature)
                    feature);
        }
    }

    @Override
    protected Feature getFeature(Feature feature) {
        if (feature instanceof DomainFeature){
            return new PortFeature((DomainFeature) feature);
        }
        return null;
    }

    @Override
    protected boolean wisHandon() {
        return false;
    }
}
