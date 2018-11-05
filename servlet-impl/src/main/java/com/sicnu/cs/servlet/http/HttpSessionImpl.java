package com.sicnu.cs.servlet.http;

import com.sicnu.cs.servlet.basis.ListEnumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class HttpSessionImpl implements HttpSession {

    private ServletContext context;
    private volatile long create;
    volatile long access;
    volatile String id;
    private ConcurrentHashMap<String,Object> attributes;

    private AtomicBoolean isActive;

    private int inactiveInteral=1800;

    HttpSessionImpl(ServletContext context, String id) {
        this.context = context;
        this.id = id;
        create=access=System.currentTimeMillis();
        attributes=new ConcurrentHashMap<>();
        isActive=new AtomicBoolean(true);
    }

    @Override
    public long getCreationTime() {
        return create;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getLastAccessedTime() {
        return access;
    }

    @Override
    public ServletContext getServletContext() {
        return context;
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        if (interval<0){
            throw new IllegalArgumentException(" interal should great zero");
        }
        this.inactiveInteral=interval;
    }

    @Override
    public int getMaxInactiveInterval() {
        return inactiveInteral;
    }

    @Override
    public HttpSessionContext getSessionContext() {
        return null;
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public Object getValue(String name) {
        return getAttribute(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        List<String> list=new ArrayList<>(attributes.keySet());
        return new ListEnumeration<>(list);
    }

    @Override
    public String[] getValueNames() {
        List<String> list=new ArrayList<>(attributes.keySet());
        return list.toArray(new String[]{});
    }

    @Override
    public void setAttribute(String name, Object value) {
        attributes.put(name,value);
    }

    @Override
    public void putValue(String name, Object value) {
        setAttribute(name,value);
    }

    @Override
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    @Override
    public void removeValue(String name) {
        removeAttribute(name);
    }

    @Override
    public void invalidate() {
        if (!isActive.compareAndSet(true,false)){
            throw new IllegalStateException();
        }
        context=null;
        attributes.clear();
    }

    @Override
    public boolean isNew() {
        return create==access;
    }


}
