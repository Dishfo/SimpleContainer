package com.cs.sicnu.core.process;

/**
 *
 * 容器接口
 *
 */
public interface Container {

    int created=0x1;
    int initing=0x2;
    int inited=0x3;
    int starting=0x4;
    int started=0x5;
    int running=0x6;
    int stopping=0x7;
    int stopped=0x8;

    int initfail=0x15;
    int startfail=0x16;
    int stopfail=0x17;

    void init();

    void start();

    void stop();

    int getLifeState();

    void addChild(Container container);
    void setParent(Container container);
    Container getParent();

    Container[] getChilds();

}
