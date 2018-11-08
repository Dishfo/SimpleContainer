package com.sicnu.cs.component;

import org.junit.Test;

public class DishTest {

    @Test
    public void test(){
        Dish dish=new Dish();
        System.setProperty("DISH_BASE_CONFIG","/home/dishfo/文档/dish-config.xml");
        dish.start();
    }
}
