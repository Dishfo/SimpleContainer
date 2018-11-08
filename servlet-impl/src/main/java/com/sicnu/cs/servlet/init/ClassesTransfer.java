package com.sicnu.cs.servlet.init;

import com.sicnu.cs.servlet.container.SimpleContext;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.util.HashMap;
import java.util.List;

/**
 *
 * 遍历给定context空间 加载符合要求的类
 *
 */
public class ClassesTransfer {

    public void findClass(SimpleContext context){
        ClassFinder finder=new ClassFinderImpl(context.getClassLoader());
        HashMap<String,WebFilter> foundedfileter=new HashMap<>();
        List<Class> cls=finder.find(context.getClasspath());

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
            }
        }

        foundedfileter.forEach((s, webFilter) -> {
            FilterRegistration dynamic = context.getFilterRegistration(s);
            dynamic.addMappingForServletNames(null,false,webFilter.servletNames());
            dynamic.addMappingForUrlPatterns(null,false,webFilter.urlPatterns());
        });

    }
}
