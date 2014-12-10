package edu.ucsb.cs;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nevena on 12/9/14.
 */
public class MessengerConf {

    Map<Integer, Messenger> messengerConfigurations = new HashMap<Integer, Messenger>();

    public void setupMessangers(){
        messengerConfigurations.put(1, new Messenger("54.183.94.22", 9090, "us-west"));
        messengerConfigurations.put(2, new Messenger("54.174.254.189", 9090, "us-east"));
        messengerConfigurations.put(3, new Messenger("54.72.205.132", 9090, "europe"));
        messengerConfigurations.put(4, new Messenger("54.94.153.129", 9090, "south"));
        messengerConfigurations.put(5, new Messenger("54.169.74.145", 9090, "singapore"));
    }

    public Map<Integer, Messenger> getmessengerConfigurations() {
        return messengerConfigurations;
    }

    public void setmessengerConfigurations(Map<Integer, Messenger> messengerConfigurations) {
        this.messengerConfigurations = messengerConfigurations;
    }
}