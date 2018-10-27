package com.cs.sicnu.core.process;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseContainer implements Container {
    protected int lifestate=created;
    private List<Container> children=new ArrayList<>();
    protected Container parent;

    @Override
    public void init() {
        lifestate=initing;
        initInteral();
        Container[] childs=getChilds();
        for (Container c:childs){
            c.init();
        }
        lifestate=inited;
    }

    protected void initInteral(){

    }

    protected void startInteral(){

    }

    protected void stopInteral(){

    }

    @Override
    public void start() {
        lifestate=starting;
        startInteral();
        Container[] childs=getChilds();
        for (Container c:childs){
            c.start();
        }
        lifestate=started;
    }


    @Override
    public void stop() {
        lifestate=stopping;
        Container[] childs=getChilds();
        for (Container c:childs){
            c.stop();
        }
        stopInteral();
        lifestate=stopped;
    }


    @Override
    public int getLifeState() {
        return lifestate;
    }

    @Override
    public void addChild(Container container) {
        container.setParent(this);
        children.add(container);
    }

    @Override
    public void setParent(Container container) {
        this.parent=container;
    }

    @Override
    public Container getParent() {
        return parent;
    }

    @Override
    public Container[] getChilds() {
        return children.toArray(new Container[]{});
    }
}
