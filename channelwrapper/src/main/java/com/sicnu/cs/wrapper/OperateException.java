package com.sicnu.cs.wrapper;

import java.io.IOException;

public class OperateException extends IOException {




    public ChannelWrapper source;



    public OperateException(String message, ChannelWrapper source) {
        super(message);
        this.source = source;
    }

    public ChannelWrapper getChannel(){
        return source;
    }

}
