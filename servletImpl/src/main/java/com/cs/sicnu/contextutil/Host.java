package com.cs.sicnu.contextutil;

import com.cs.sicnu.core.process.Port;
import com.cs.sicnu.core.protocol.HttpResponse;
import com.cs.sicnu.http.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 代表一个ip或一个域名的子服务器
 *
 */
public class Host extends PortContainer implements Port {

    private Logger logger= LogManager.getLogger(getClass().getName());

    private String domain;

    Host(String domain) {
        this.domain = domain;
    }

    private void addContext(Context context){
        addChild(context);
    }

    @Override
    protected void initInteral() {
        super.initInteral();
        //todo 仅供测试
        addContext(new Context("" +
                "/home/dishfo/mydata/IdeaProjects/servlettest/out/artifacts/servlettest_Web_exploded",
                "/test"));
    }

    public String getDomain() {
        return domain;
    }

    @Override
    protected void startInteral() {
        super.startInteral();
    }

    @Override
    public void start() {
        super.start();
    }


    @Override
    public void stop() {
        super.stop();
    }


    @Override
    public void process(Object o) {
        if (o instanceof HttpResponse) {
            logger.debug("get a response");
            getParent().process(o);
        }
    }

    @Override
    protected void registerFeature(Feature feature) {}

    @Override
    protected Feature getFeature(Feature feature) {
        if (feature instanceof ContextFeature){
            return new DomainFeature(domain, (ContextFeature) feature);
        }
        return null;
    }

    @Override
    protected boolean wisHandon() {
        return true;
    }
}
