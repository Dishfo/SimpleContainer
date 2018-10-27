package com.cs.sicnu.http;

import javax.servlet.*;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


/**
 *
 *
 *
 */
public class ServletAsyncContext implements AsyncContext {
    private ServletRequest request;
    private ServletResponse response;
    private ReentrantLock lock;
    private Condition competeCondition;
    private AtomicBoolean isover;

    public ServletAsyncContext(ServletRequest request, ServletResponse response) {
        this.request = request;
        this.response = response;
        lock=new ReentrantLock();
        isover=new AtomicBoolean(false);
        competeCondition=lock.newCondition();
    }

    @Override
    public ServletRequest getRequest() {
        return request;
    }

    @Override
    public ServletResponse getResponse() {
        return response;
    }

    @Override
    public boolean hasOriginalRequestAndResponse() {
        return (request!=null&&response!=null);
    }

    @Override
    public void dispatch() {

    }

    @Override
    public void dispatch(String path) {

    }

    @Override
    public void dispatch(ServletContext context, String path) {

    }

    @Override
    public void complete() {
        try {
            if (isover.get()){
                response.flushBuffer();
            }else {
                try {
                    lock.lock();
                    competeCondition.await();
                } catch (InterruptedException ignored) {

                }finally {
                    lock.unlock();
                }
                response.flushBuffer();
            }
        }catch (IOException e){}
    }

    @Override
    public void start(Runnable run) {
        new Thread(() -> {
            run.run();
            isover.set(true);
            //NOTIIF LISTENRE
            try {
                lock.lock();
                competeCondition.signalAll();
            }finally {
                lock.unlock();
            }

        }).start();
    }

    @Override
    public void addListener(AsyncListener listener) {

    }

    @Override
    public void addListener(AsyncListener listener, ServletRequest servletRequest, ServletResponse servletResponse) {

    }

    @Override
    public <T extends AsyncListener> T createListener(Class<T> clazz) throws ServletException {
        return null;
    }

    @Override
    public void setTimeout(long timeout) {

    }

    @Override
    public long getTimeout() {
        return 0;
    }
}
