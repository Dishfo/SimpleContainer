package com.sicnu.cs.wrapper;

import java.nio.ByteBuffer;

/**
 *
 * 针对wrapper的创建 read write close 进行监听
 */
public interface WrappersListener {

    void onWrapperCreated(ChannelWrapper wrapper);

    void onWrapperRead(ChannelWrapper wrapper, ByteBuffer[] data);

    void onWrapperWrite(ChannelWrapper wrapper);


}
