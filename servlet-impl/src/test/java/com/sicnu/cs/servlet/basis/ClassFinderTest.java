package com.sicnu.cs.servlet.basis;

import com.sicnu.cs.servlet.container.*;
import org.junit.Test;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClassFinderTest {

    @Test
    public void test() throws UnknownHostException {

        BaseEngine engine=new BaseEngine();
        SimpleHost host=new SimpleHost();
        engine.addChild(host);
        host.addAddress(InetAddress.getByName("localhost"));

        SimpleContext context=new SimpleContext("/home/dishfo/mydata/IdeaProjects/servlettest/out/artifacts/servlettest_Web_exploded","/test");
        host.addChild(context);
        ClassFinder finder=new
                ClassFinderImpl(ContextClassLoader.getClassLoader("/home/dishfo/mydata/IdeaProjects/servlettest/out/" +
                "artifacts/servlettest_Web_exploded/WEB-INF/classes"));
        context.init();
        HashMap<String,WebFilter> foundedfileter=new HashMap<>();
        List<Class> cls=finder.find("/home/dishfo/mydata/IdeaProjects/servlettest/target/classes");
        for (Class c:cls){
            if (HttpServlet.class.isAssignableFrom(c)){
                WebServlet servlet= (WebServlet) c.getAnnotation(WebServlet.class);

                ServletRegistration.Dynamic dynamic=
                        context.addServlet(servlet.name(),c);
                dynamic.setLoadOnStartup(servlet.loadOnStartup());
                dynamic.setAsyncSupported(servlet.asyncSupported());
                WebInitParam[] initParams=servlet.initParams();
                for (WebInitParam param:initParams){
                   dynamic.setInitParameter(param.name(),param.value());
                }
                String []urls=servlet.urlPatterns();
                for (String s:urls){
                    dynamic.addMapping(s);
                }

            }else {
                WebFilter filter= (WebFilter) c.getAnnotation(WebFilter.class);
                FilterRegistration.Dynamic dynamic=
                        context.addFilter(filter.filterName(),c);
                foundedfileter.putIfAbsent(filter.filterName(),filter);
       ;
            }
        }

        foundedfileter.forEach((s, webFilter) -> {
            FilterRegistration dynamic = context.getFilterRegistration(s);
            dynamic.addMappingForServletNames(null,false,webFilter.servletNames());
            dynamic.addMappingForUrlPatterns(null,false,webFilter.urlPatterns());
        });
        context.start();
        System.out.println(cls.size() +"  "+context.getLifeState());




    }

}
