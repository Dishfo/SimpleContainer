package com.cs.sicnu.core.protocol;

import java.nio.ByteBuffer;

/**
 *
 * represent a protocol handler;
 *
 * @param <O>
 */
public interface Protocol<O>{

    void receive(ByteBuffer buffer);
    void receive(ByteBuffer[] buffers);

    void resolve(ByteBuffer buffer);
    void resolve(ByteBuffer[] buffers);

    void outPut(O obj);

}


