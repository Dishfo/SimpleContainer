package com.cs.sicnu.core.process;

/**
 *
 * 容器接口
 *
 */
public interface Container {

    public final static int created=0x1;
    public final static int initing=0x2;
    public final static int inited=0x3;
    public final static int starting=0x4;
    public final static int started=0x5;
    public final static int running=0x6;
    public final static int stopping=0x7;
    public final static int stopped=0x8;

    void init();

    void start();

    void stop();

    int getLifeState();

    void addChild(Container container);
    void setParent(Container container);
    Container getParent();

    Container[] getChilds();

}
