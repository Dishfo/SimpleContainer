package com.sicnu.cs.wrapper;

public class WriteOpException  extends OperateException{

    public static final int IO_ERROR=1;
    public static final int CLOSE_CONN=2;


    int occur;

    public WriteOpException(String message, ChannelWrapper source,int occur) {
        super(message, source);
        this.occur=occur;
    }

    public int getOccur(){
        return occur;
    }
}
