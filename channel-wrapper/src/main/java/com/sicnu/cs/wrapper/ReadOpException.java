package com.sicnu.cs.wrapper;

public class ReadOpException extends OperateException {

    public static final int PEERCLOSE=1;
    public static final int NO_DATA=2;
    public static final int UN_KNOW=4;

    private int occur;

    public ReadOpException(String message, ChannelWrapper source,int occur) {
        super(message, source);
        this.occur=occur;
    }

    public int getOccur(){
        return occur;
    }
}
