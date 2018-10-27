package com.sicnu.cs.component;

import com.cs.sicnu.contextutil.HandleEngine;
import com.cs.sicnu.core.process.Container;
import com.cs.sicnu.core.process.Engine;
import com.cs.sicnu.core.process.Poster;
import com.cs.sicnu.core.protocol.Connection;
import com.cs.sicnu.core.protocol.HttpRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 统一管理所有的engine
 * 把一个request分发给对应域名的
 * engine
 *
 */
public class EngineManager implements Engine{

    private static final EngineManager instance=new EngineManager();
    //额外的读写保证
    private HashMap<String, Engine> registeredEngines=new HashMap<>();

    private EngineManager(){}

    void addEngine(String host, Engine engine){
        registeredEngines.put(host,engine);
    }

    @Override
    public void handle(Connection connection,HttpRequest request) {
        for (Map.Entry<String,Engine> e:registeredEngines.entrySet()){
            e.getValue().handle(connection,request);
        }
    }

    public void startAllEngine(){
        registeredEngines.forEach((s, engine) -> {
            if (engine instanceof Container){
                ((Container) engine).init();
                ((Container) engine).start();
            }
        });
    }

    static {
        instance.registeredEngines.put("test",new HandleEngine());

    }

    static EngineManager getInstance() {
        return instance;
    }
}
