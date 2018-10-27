package com.sicnu.cs.component;

import com.cs.sicnu.core.process.Engine;
import com.cs.sicnu.core.protocol.Connection;
import com.cs.sicnu.core.protocol.HttpRequest;
import com.cs.sicnu.http.Context;
import com.cs.sicnu.http.HttpRequestWrapper;
import com.sicnu.cs.wrapper.HttpConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;

/**
 * 用于 http 协议的localhost的engine
 *
 */
public class NormalEngine implements Engine {

    private Logger logger= LogManager.getLogger(getClass().getName());
    private Context contexts[];

    NormalEngine() {
        Context context=new Context("/home/dishfo/mydata/IdeaProjects/servlettest/target/classes",
                "/test");

        contexts=new Context[]{context};
    }

    @Override
    public void handle(Connection connection,HttpRequest request) {
        logger.debug("begin to handle the request");

        if (!(connection instanceof HttpConnection)){
            return;
        }

////        HttpConnection httpConnection= (HttpConnection) connection;
//
////        HttpRequestWrapper wrapper=new HttpRequestWrapper(null);
//        wrapper.init(request);
//        URI uri=wrapper.getRequestUri();
//        String path=uri.getPath();
//
//        for (Context context:contexts){
//            if (path.startsWith(context.getContextPath()+"/")){
//                wrapper.setContext(context);
////                context.post(wrapper);
//            }
//            break;
//        }
    }
}
