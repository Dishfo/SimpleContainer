package com.sicnu.cs.component;

import com.cs.sicnu.core.process.Container;
import com.cs.sicnu.core.utils.StringUtils;
import com.sicnu.cs.http.HttpConnection;
import com.sicnu.cs.servlet.container.Engine;
import com.sicnu.cs.wrapper.ChannelWrapper;
import com.sicnu.cs.wrapper.ReadOpException;
import com.sicnu.cs.wrapper.WrappersListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

public class Dish {

    private Logger logger = LogManager.getLogger(getClass().getName());
    private ConfigInit init;

    private MessageListener listener;
    private Engine engine;

    public void start() {
        String config = System.getenv("DISH_BASE_CONFIG");
        logger.debug("config in "+config);
        if (StringUtils.isEmpty(config)) {
            return;
        } else {
            init = new ConfigInit();
            File file=new File(config);
            logger.debug(file.getAbsolutePath());
            init.config(file.getAbsolutePath());
            listener = init.getListener();
            engine = init.getEngine();
            Objects.requireNonNull(listener);
            Objects.requireNonNull(engine);
            listener.setChannelListener(new ChannelListener());
            if (engine instanceof Container){
                Container container= (Container) engine;
                container.start();
                if (container.getLifeState()!=Container.running){
                    logger.debug("start container failed");
                    return;
                }
            }
            listener.startListen();
        }
    }


    private class ChannelListener implements WrappersListener{

        @Override
        public void onWrapperCreated(ChannelWrapper wrapper) {
            System.out.println("the wrapper create");
            HttpConnection connection=new HttpConnection(wrapper);
            connection.setHandler((connection1, request, response) -> {
                engine.handleRequset(connection,request,response);
            });
            wrapper.setAttributes("conn",connection);
        }

        @Override
        public void onWrapperRead(ChannelWrapper wrapper, ByteBuffer[] data, Exception e) {
            if (e instanceof ReadOpException){
                if (((ReadOpException) e).getOccur()==ReadOpException.PEERCLOSE){
                    try { wrapper.close(); } catch (IOException ignored) {}
                }
            }else {
                HttpConnection conn= (HttpConnection)
                        wrapper.getAttribute("conn");
                conn.receice(data);
            }
        }

        @Override
        public void onWrapperWrite(ChannelWrapper wrapper, Exception e) {

        }
    }


}
