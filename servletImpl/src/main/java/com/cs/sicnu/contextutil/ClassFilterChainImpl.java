package com.cs.sicnu.contextutil;

import java.util.ArrayList;
import java.util.List;

public class ClassFilterChainImpl implements ClassFilterChain{

    List<ClassFilter> filters=new ArrayList<>();

    @Override
    public boolean accept(Class cls) {
        for (ClassFilter filter:filters){
            if (filter.isAccept(cls)){
                return true;
            }
        }

        return false;
    }

    public void addFilter(ClassFilter filter){
        filters.add(filter);
    }

}
