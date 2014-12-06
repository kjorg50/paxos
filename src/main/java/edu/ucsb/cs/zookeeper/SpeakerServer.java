package edu.ucsb.cs.zookeeper;

/**
 * Created by nevena on 12/5/14.
 */

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SpeakerServer {

    //final static Logger logger = LoggerFactory.getLogger(SpeakerServer.class);
    static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(SpeakerServer.class);
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static ZookeeperMain monitor;

    private static void printUsage() {
        System.out.println("program [message] [wait between messages in millisecond]");
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            printUsage();
            System.exit(1);
        }

        long delay = Long.parseLong(args[1]);

        Speaker speaker = null;

        try {
            speaker = new Speaker(args[0]);
            monitor = new ZookeeperMain();
            monitor.setListener(speaker);
            monitor.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        scheduler.scheduleWithFixedDelay(speaker, 0, delay, TimeUnit.MILLISECONDS);
        System.out.println("Speaker server started with fixed time delay of " + delay + " milliseconds.");
    }
}
