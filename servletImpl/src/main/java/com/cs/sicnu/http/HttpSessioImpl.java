package com.cs.sicnu.http;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Enumeration;
import java.util.HashMap;

public class HttpSessioImpl implements HttpSession {

    protected long creationtime;
    protected long lastaccess;
    private String id;
    private Context context;
    private HashMap<String,Object> attributies=new HashMap<>();

    public HttpSessioImpl(String id, Context context) {
        this.id = id;
        this.context = context;
        creationtime=lastaccess=System.currentTimeMillis();
    }

    @Override
    public long getCreationTime() {
        return creationtime;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getLastAccessedTime() {
        return lastaccess;
    }

    @Override
    public ServletContext getServletContext() {
        return context;
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        throw new UnsupportedOperationException("setMaxInactiveInterval(int interval)");
    }

    @Override
    public int getMaxInactiveInterval() {
        throw new UnsupportedOperationException("getMaxInactiveInterval()");
    }

    @Override
    public HttpSessionContext getSessionContext() {
        return null;
    }

    @Override
    public Object getAttribute(String name) {
        return attributies.get(name);
    }

    @Override
    public Object getValue(String name) {
        return null;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return new Context.IteratorEnumeration<>(attributies.keySet().iterator());
    }

    @Override
    public String[] getValueNames() {
        return new String[0];
    }

    @Override
    public void setAttribute(String name, Object value) {
        attributies.put(name,value);
    }

    @Override
    public void putValue(String name, Object value) {

    }

    @Override
    public void removeAttribute(String name) {
        attributies.remove(name);
    }

    @Override
    public void removeValue(String name) {

    }

    @Override
    public void invalidate() {
        attributies.clear();
        context=null;
    }

    @Override
    public boolean isNew() {
        return lastaccess==creationtime;
    }
}
