package com.sicnu.cs.wrapper;

import java.nio.channels.SelectionKey;
import java.util.Set;

public interface ChannelMananger {
    void processKeys(Set<SelectionKey> keys);
    void init();
    void stop();
    void setWrapLinstener(WrappersListener listener);
}
