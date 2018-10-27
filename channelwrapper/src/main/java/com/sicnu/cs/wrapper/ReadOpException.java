package com.sicnu.cs.wrapper;

public class ReadOpException extends OperateException {

    public ReadOpException(String message, ChannelWrapper source) {
        super(message, source);
    }
}
