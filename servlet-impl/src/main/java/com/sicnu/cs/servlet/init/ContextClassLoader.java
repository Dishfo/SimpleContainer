package com.sicnu.cs.servlet.init;

import com.cs.sicnu.core.utils.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

public class ContextClassLoader extends ClassLoader{
    private String basePath;
    private Logger logger= LogManager.getLogger(getClass().getName());

    public ContextClassLoader(ClassLoader parent, String basePath) {
        super(parent);
        this.basePath = basePath;
        logger.debug("class loader base path is "+basePath);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] bytes=findData(name);
        logger.debug("to find class "+name);
        if (bytes!=null){
            logger.debug("find class "+name);
            return defineClass(name,bytes,0,bytes.length);
        }else {
            throw new ClassNotFoundException(" class Name is "+name);
        }
    }

    private byte[] findData(String classname)  {
        String file= StringUtils.getClassPath(basePath,classname);
        File f=new File(file);
        try {
            FileInputStream fileInputStream=new FileInputStream(f);
            byte[] bytes=new byte[fileInputStream.available()];
            int len=fileInputStream.read(bytes);
            if (len<=0){
                return null;
            }
            return bytes;
        } catch (IOException e) {
            return null;
        }
    }

    private static HashMap<String,ContextClassLoader> loaderMap=new HashMap<>();

    public static ContextClassLoader getClassLoader(String basePath){
        ContextClassLoader loader=loaderMap.get(basePath);
        if (loader==null){
            loader=new ContextClassLoader(getSystemClassLoader(),basePath);
        }
        return loader;
    }


}
