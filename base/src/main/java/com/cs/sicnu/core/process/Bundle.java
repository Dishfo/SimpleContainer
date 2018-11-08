package com.cs.sicnu.core.process;

import java.util.HashMap;

/**
 * 用于把多个类打包
 *
 */
public class Bundle {
    private HashMap<String,Object> datas=new HashMap<>();


    public void putData(String name,Object value){
        datas.put(name,value);
    }

    public Object getData(String name){
        return datas.get(name);
    }
}
