package com.cs.sicnu.core.process;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseContainer implements Container {
    private int lifestate = created;
    private List<Container> children = new ArrayList<>();
    private Container parent;

    @Override
    public void init() {
        if (lifestate >= initing) {
            throw new IllegalArgumentException("the containe has inited or is initing");
        } else {
            lifestate = initing;
            try {
                initInteral();
            } catch (Throwable throwable) {
                lifestate = initfail;
                return;
            }

            if (lifestate == initfail) {
                return;
            }

            for (Container c : children) {
                try {
                    c.init();
                    if (c.getLifeState() != inited
                            && needAllCompete()) {
                        lifestate = initfail;
                        return;
                    }
                } catch (Throwable t) {
                    if (c.getLifeState() != inited) {
                        lifestate = initfail;
                        return;
                    }
                }
            }
            lifestate = inited;
        }
    }

    protected void initInteral() {

    }

    protected void startInteral() {

    }

    protected void stopInteral() {

    }

    @Override
    public void start() {
        if (lifestate >= starting) {
            throw new IllegalArgumentException("the containe has started or is stated");
        }

        if (lifestate == created) {
            init();
        }

        if (lifestate == inited) {
            try {
                startInteral();
            } catch (Throwable t) {
                lifestate = startfail;
                return;
            }

            if (lifestate == startfail) {
                return;
            }

            for (Container c : children) {

                try {
                    c.start();
                    if (c.getLifeState() >=initfail
                            && needAllCompete()) {
                        lifestate = startfail;
                        return;
                    }

                } catch (Throwable throwable) {
                    if (c.getLifeState() > initfail &&
                            needAllCompete()) {
                        lifestate = startfail;
                        return;
                    }
                }
            }
            lifestate=started;
        }


        if (lifestate == started) {
            lifestate = running;
        }

    }


    @Override
    public void stop() {
        if (lifestate >= stopping) {
            throw new IllegalArgumentException("the container has stop or is stopping");
        }

        lifestate = stopping;

        for (Container c : children) {
            try {
                c.stop();
            } catch (Throwable ignored) {
            }
        }
        try {
            stopInteral();
        } catch (Throwable throwable) {
            lifestate = stopfail;
        }
        if (lifestate == stopfail) {
            return;
        }
        lifestate = stopped;
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
    public Container getParent() {
        return parent;
    }

    @Override
    public void setParent(Container container) {
        this.parent = container;
    }

    @Override
    public Container[] getChilds() {
        return children.toArray(new Container[]{});
    }


    /**
     * @return true all child of  container running ,we talk this container run correctly
     */
    protected boolean needAllCompete() {
        return true;
    }
}
