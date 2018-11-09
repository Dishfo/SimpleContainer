package com.sicnu.cs.servlet.init;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public class ClassFinderImpl implements ClassFinder {

    private Logger logger= LogManager.getLogger(getClass().getName());
    private ClassLoader classLoader;
    private Executor executor;
    private List<ClassFilter> filters;
    private static final String CLASS_SUFFIX=".class";

    public ClassFinderImpl(ClassLoader classLoader) {
        this.classLoader = classLoader;
        List<ClassFilter> list=
                new ArrayList<>();

        list.add(cls -> {
            if (HttpServlet.class.isAssignableFrom(cls)){
                WebServlet servlet=
                        (WebServlet) cls.getAnnotation(WebServlet.class);
                return servlet != null;
            }
            return false;
        });

        list.add(cls -> {
            if (HttpFilter.class.isAssignableFrom(cls)){
                WebFilter filter=
                        (WebFilter) cls.getAnnotation(WebFilter.class);
                return filter != null;
            }
            return false;
        });
        filters= Collections.unmodifiableList(list);
    }

    private String classpath;
    @Override
    public synchronized List<Class> find(String path) {
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        classpath=path;
        File clsfile=new File(classpath);
        if (!clsfile.exists()){
            return new ArrayList<>();
        }else {
            classpath=clsfile.getAbsolutePath();
        }

        logger.debug("start find "+path);
        List<Class> res = new ArrayList<>();
        List<Class> futures = findInteral(classpath);
        for (Class c : futures) {
            if (c != null) {
                res.add(c);
            }
        }
        return res;
    }

    private List<Class> findInteral(String path) {
        List<Class> res = new ArrayList<>();
        File file=new File(path);
        logger.debug("want to find file is "+file.getAbsolutePath()+file.exists());
        logger.debug("is file ?"+file.isFile());
        if (file.exists()){
            if (file.isDirectory()){
                List<Future<List<Class>>> classes
                        =new ArrayList<>();
                File[] files = file.listFiles((File filepath) ->
                        filepath.isDirectory() || filepath.getName().endsWith(CLASS_SUFFIX));
                assert files != null;
                for (File f : files) {
                    final  File constf=f;

                    FutureTask<List<Class>> task=
                            new FutureTask<>(() -> findInteral(constf.getAbsolutePath()));
                    classes.add(task);
                    executor.execute(task);
                }

                for (Future<List<Class>> f:classes){
                    try {
                        res.addAll(f.get());
                    } catch (InterruptedException | ExecutionException ignored) {}
                }

            }else if (file.isFile()){
                try {
                    logger .debug("a file named "+file.getAbsolutePath());
                    Class cls = classLoader.loadClass(getClassName(file.getAbsolutePath(),
                            classpath));

                    for (ClassFilter filter:filters){
                        if (filter.isAccept(cls)){
                            res.add(cls);
                        }
                    }
                } catch (ClassNotFoundException e) {
                    logger.error(e.toString());
                }
            }
        }
        return res;
    }

    private String getClassName(String file, String base) {

        if (file.startsWith(base)&&file.endsWith(CLASS_SUFFIX)){
            String className=file.substring(base.length(),file.length() - CLASS_SUFFIX.length());
            if (className.startsWith("/")){
                className=className.substring(1);
            }
            return className.replaceAll("/","\\.");
        }
        return null;
    }
}
