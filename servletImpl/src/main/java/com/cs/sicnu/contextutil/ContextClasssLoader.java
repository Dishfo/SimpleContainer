package com.cs.sicnu.contextutil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ContextClasssLoader extends ClassLoader {

    private String classpath;

    public ContextClasssLoader(ClassLoader parent,String base) {
        super(parent);
        classpath =base;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return super.loadClass(name);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> cls=null;
        String path=getPath(name);
        try {
            byte[] bytes=loadData(path);
            cls=defineClass(name,bytes,0,bytes.length);
        } catch (IOException e) {
            throw new ClassNotFoundException("read class file failed");
        }
        return cls;
    }



    /**
     * 不保证路劲真实存在
     * @param className
     * @return
     */

    private String getPath(String className){
        StringBuilder builder=new StringBuilder(classpath +"/");
        String[] dirs=className.split("\\.");
        for (String s:dirs){
            builder.append(s)
                    .append("/");
        }
        builder.setCharAt(builder.length()-1,'.');
        builder.append("class");
        return builder.toString();
    }

    private byte[] loadData(String path) throws IOException {
        FileInputStream inputStream=new FileInputStream(new File(path));
        byte[] bytes=new byte[inputStream.available()];
        inputStream.read(bytes);
        inputStream.close();
        return bytes;
    }


}
