package edu.ucsb.cs;

import java.util.Map;

/**
 * Created by nevena on 12/9/14.
 */
public class Messenger {
    String address;
    Integer port;
    String description;

    Messenger(String address, Integer port, String description){
        this.address = address;
        this.port = port;
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
