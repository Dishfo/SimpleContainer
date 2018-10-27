package com.sicnu.cs.wrapper;

public class FailOprException extends OperateException{


    public FailOprException(String message, ChannelWrapper source) {
        super(message, source);
    }
}
