package com.sicnu.cs.servlet.basis;

import com.sicnu.cs.servlet.container.ClassFinderImpl;
import com.sicnu.cs.servlet.container.ContextClassLoader;
import org.junit.Test;

import java.util.List;

public class ClassFinderTest {

    @Test
    public void test(){
        ContextClassLoader contextClassLoader=
                new ContextClassLoader(ClassLoader.getSystemClassLoader(),"/home/dishfo/mydata/IdeaProjects/servlettest/target/classes");
        ClassFinder finder=new ClassFinderImpl(contextClassLoader);

        List<Class> cls=finder.find("/home/dishfo/mydata/IdeaProjects/servlettest/target/classes");

        System.out.println(cls.size());

    }

}
