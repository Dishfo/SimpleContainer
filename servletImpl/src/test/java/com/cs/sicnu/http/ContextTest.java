package com.cs.sicnu.http;

import com.cs.sicnu.contextutil.HandleEngine;
import com.cs.sicnu.contextutil.Host;
import com.cs.sicnu.core.protocol.HttpRequest;
import org.junit.Test;

public class ContextTest {
    @Test
    public void test(){
        Context context=new Context("/home/dishfo/mydata/IdeaProjects/servlettest/target/classes","/");

        System.out.println("context has statrt");
        HttpRequest request=RequsetGnerater.create("/home/dishfo/logs/request--1538556552050");

        HandleEngine engine=new HandleEngine();
        engine.init();
        engine.start();
        engine.handle(null,request);
    }
}
