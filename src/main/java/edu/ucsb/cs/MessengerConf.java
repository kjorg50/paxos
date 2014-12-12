package edu.ucsb.cs;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nevena on 12/9/14.
 */
public class MessengerConf {

    public Map<Integer, Messenger> messengerConfigurations = new HashMap<Integer, Messenger>();

    public MessengerConf() {
//        messengerConfigurations.put(0, new Messenger("localhost", 9090, "us-west"));
//        messengerConfigurations.put(1, new Messenger("localhost", 9091, "us-east"));
        messengerConfigurations.put(0, new Messenger("54.183.94.22", 9090, "us-west"));
        messengerConfigurations.put(1, new Messenger("54.174.254.189", 9090, "us-east"));
        messengerConfigurations.put(2, new Messenger("54.154.90.72", 9090, "europe"));
//        messengerConfigurations.put(3, new Messenger("54.169.74.145", 9090, "singapore"));
//        messengerConfigurations.put(4, new Messenger("54.94.153.129", 9090, "south"));
    }

    public Messenger getOneMessenger(int i) {
        return messengerConfigurations.get(i);
    }

    public Map<Integer, Messenger> getMessengerConfigurations() {
        return messengerConfigurations;
    }

    public void setMessengerConfigurations(Map<Integer, Messenger> messengerConfigurations) {
        this.messengerConfigurations = messengerConfigurations;
    }
}
