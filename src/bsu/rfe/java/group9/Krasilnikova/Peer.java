package bsu.rfe.java.group9.Krasilnikova;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class Peer {
    private String name;
    private String address;

    public Peer (String name, String address) {
        this.name = name;
        this.address = address;
    }
    public String getName() {
        return name;
    }
    public String getAddress() {
        return address;
    }
    public Peer(String senderName, SocketAddress remoteSocketAddress) {
    }

}