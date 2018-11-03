package com.sicnu.cs.servlet.container;

import javax.servlet.Registration;
import java.util.*;

public class RegistraContainer extends RegisterContainer implements Registration.Dynamic {

    private String name;
    Class cls;
    private HashMap<String,String> initParameters;
    boolean isAsync;


    RegistraContainer(String name) {
        this.name = name;
        isAsync=false;
        initParameters=new HashMap<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getClassName() {
        if (cls==null){
            return null;
        }else {
            return cls.getName();
        }
    }

    @Override
    public boolean setInitParameter(String name, String value) {
        return initParameters.putIfAbsent(name,value)==null;
    }

    @Override
    public String getInitParameter(String name) {
        return initParameters.get(name);
    }

    @Override
    public Set<String> setInitParameters(Map<String, String> initParameters) {
        Set<String> confilct=new HashSet<>();
        initParameters.forEach((k,v) -> {
            Object res=this.initParameters.putIfAbsent(k,v);
            if (res!=null){
                confilct.add(k);
            }
        });

        return confilct;

    }
    public Map<String,String> getInitParameters(){
        return Collections.unmodifiableMap(initParameters);
    }
    @Override
    public void setAsyncSupported(boolean isAsyncSupported) {
        isAsync=isAsyncSupported;
    }
}
