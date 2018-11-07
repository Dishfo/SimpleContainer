package com.sicnu.cs.servlet.basis.map;

import java.net.InetAddress;
import java.util.List;

public class HostNode extends Node<ContextNode,RootNode>{

    private List<InetAddress> addresses;

    public HostNode(List<InetAddress> addresses) {
        this.addresses = addresses;
    }

    public List<InetAddress> getAddresses() {
        return addresses;
    }
}













