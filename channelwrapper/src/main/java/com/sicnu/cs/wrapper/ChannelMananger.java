package com.sicnu.cs.wrapper;

import java.nio.channels.SelectionKey;
import java.util.Set;

public interface ChannelMananger {
    void processKeys(Set<SelectionKey> keys);
    void setWrapLinstener(WrappersListener listener);
}
