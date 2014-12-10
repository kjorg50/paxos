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
}
