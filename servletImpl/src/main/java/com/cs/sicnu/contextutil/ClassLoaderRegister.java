package com.cs.sicnu.contextutil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对已有的classload 进行统一管理
 * concurrentHashMap
 */
public class ClassLoaderRegister {

    private static final ClassLoaderRegister INSTANCE=new ClassLoaderRegister();


    private ConcurrentHashMap<String,ClassLoader> classLoaders
            =new ConcurrentHashMap<>();

    public static final String SYSTEM="system_classloader";

    private ClassLoaderRegister(){
        try {
            register(SYSTEM,ClassLoader.getSystemClassLoader());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public synchronized void register(String name, ClassLoader classLoader) throws IllegalAccessException{
        Objects.requireNonNull(classLoader);
        if (classLoaders.containsKey(name)){
            throw new IllegalAccessException("the load name conflict");
        }
        classLoaders.put(name,classLoader);
    }

    public ClassLoader register(String name,String parent,Class cls){
        ClassLoader p=classLoaders.get(parent);
        if (p==null){
            return null;
        }else {
            try {
                Constructor constructor=cls.getConstructor(p.getClass());
                ClassLoader cl =(ClassLoader) constructor.newInstance(p);
                register(name,cl);
                return cl;
            } catch (NoSuchMethodException |
                    IllegalAccessException |
                    InstantiationException |
                    InvocationTargetException e) {
                return null;
            }
        }
    }

    public ClassLoader getClassLoader(String name){
        return classLoaders.get(name);
    }

    public static ClassLoaderRegister getInstance(){
        return INSTANCE;
    }
}
