package com.sicnu.cs.servlet.container;

import com.cs.sicnu.core.utils.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ContextClassLoader extends ClassLoader{
    private String basePath;

    public ContextClassLoader(ClassLoader parent, String basePath) {
        super(parent);
        this.basePath = basePath;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] bytes=findData(name);
        if (bytes!=null){
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

}
