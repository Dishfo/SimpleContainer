package com.sicnu.cs.component;

import org.junit.Test;

public class XmlInit {

    @Test
    public void test(){
        ConfigInit init=new ConfigInit();
        init.config("/home/dishfo/文档/dish-config.xml");
        System.out.println("parse  config compete");
    }
}
