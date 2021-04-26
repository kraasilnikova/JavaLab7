package bsu.rfe.java.group9.Krasilnikova;

import java.net.InetSocketAddress;

public class Peer {
    private final String name;
    private final InetSocketAddress address;

    public Peer(String name, InetSocketAddress address) {
        this.name = name;
        this.address = address;
    }

    public InetSocketAddress getAddress() {
        return this.address;
    }

    public String getName() {
        return this.name;
    }
}