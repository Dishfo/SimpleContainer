package com.cs.sicnu.http;

import com.cs.sicnu.contextutil.ClassFinder;
import com.cs.sicnu.contextutil.ItemFinder;
import com.cs.sicnu.core.process.BaseContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.Servlet;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebServlet;
import java.util.List;
import java.util.Objects;

/**
 * 负责从指定的路径中搜索servlet
 *
 */


public class ComponetRegister extends BaseContainer {

    private Logger slogger= LogManager.getLogger(getClass().getName());

    private Context context;
    private ClassFinder classFinder;

    ComponetRegister(Context context) {
        Objects.requireNonNull(context);
        this.context = context;
    }

    protected void startInteral() {
    }

    protected void initInteral() {
        classFinder=new ItemFinder(context.getClassPath());
        List<Class> list=findServlet();
        for (Class c:list){
            if (Servlet.class.isAssignableFrom(c)){
                WebServlet webServlet= (WebServlet) c.getAnnotation(WebServlet.class);
                if (webServlet!=null&&!webServlet.name().equals("")){
                    context.addServlet(webServlet.name(),c);
                }
            }else if (Filter.class.isAssignableFrom(c)){
                WebFilter filter= (WebFilter) c.getAnnotation(WebFilter.class);
                context.addFilter(filter.filterName(),c);
            }
        }
    }

    @Override
    protected void stopInteral() {
        super.stopInteral();
    }

    private List<Class> findServlet(){
        return classFinder.find();
    }



}
