package com.cs.sicnu.core.process;

public interface CommandClinet<C> {
    int send(C cmd);

}
