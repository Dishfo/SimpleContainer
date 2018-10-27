package com.cs.sicnu.contextutil;

import com.cs.sicnu.http.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

public class ItemFinder implements ClassFinder {

    private Logger logger= LogManager.getLogger(getClass().getName());
    private String classpath;
    private ThreadPoolExecutor executor;
    private ContextClasssLoader contextClasssLoader;

    private ClassFilterChain classFilterChain;


    public ItemFinder(String classpath) {
        Objects.requireNonNull(classpath);
        this.classpath=classpath;
        contextClasssLoader = (ContextClasssLoader) ClassLoaderRegister.getInstance()
                .getClassLoader(Context.CONTEXT);

        classFilterChain=new ClassFilterChainImpl();
        ClassFilterChainImpl impl= (ClassFilterChainImpl) classFilterChain;
        impl.addFilter(cls -> {
            WebServlet servlet = (WebServlet) cls.getAnnotation(WebServlet.class);
            return servlet != null && cls.getSuperclass() == HttpServlet.class;
        });

        impl.addFilter(cls -> {
            WebFilter filter= (WebFilter) cls.getAnnotation(WebFilter.class);
            return filter!=null&&cls.getSuperclass()== HttpFilter.class;
        });
    }

    private static final String CLASS_SUFFIX = ".class";

    public synchronized List<Class> find() {
        executor = (ThreadPoolExecutor)
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
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
                    Class cls = contextClasssLoader.findClass(getClassName(file.getAbsolutePath(),
                            classpath));
                    logger.debug(cls.toString());
                    if (classFilterChain.accept(cls)){
                        res.add(cls);
                    }
                } catch (ClassNotFoundException ignored) {}
            }
        }

        return res;
    }

    private String getClassName(String file, String base) {
        if (file.startsWith(base)&&file.endsWith(CLASS_SUFFIX)){
            String className=file.substring(base.length(),file.length() - CLASS_SUFFIX.length());
//            logger.debug("servelet class Name is "+className);
            if (className.startsWith("/")){
                className=className.substring(1);
            }
            return className.replaceAll("/","\\.");
        }
        return null;
    }
}
