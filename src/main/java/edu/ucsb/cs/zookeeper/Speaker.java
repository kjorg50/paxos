package edu.ucsb.cs.zookeeper;

/**
 * Created by nevena on 12/5/14.
 */

        import org.apache.zookeeper.*;
        import java.io.BufferedWriter;
        import java.io.FileWriter;
        import java.io.IOException;
        import java.lang.management.ManagementFactory;


public class Speaker implements Runnable, ZookeeperMain.NodeMonitorListener {

    private String message;
    private String processName;
    private long counter = 0;
    private volatile boolean canSpeak = false;

    public Speaker(String message) throws IOException, InterruptedException, KeeperException {
        this.message = message;
        this.processName = getUniqueIdentifier();
    }

    private static String getUniqueIdentifier() {
        String processName = ManagementFactory.getRuntimeMXBean().getName();
        String processId = processName.substring(0, processName.indexOf("@"));
        return "pid-" + processId + ".";
    }

    public void run() {
        try {
            if (canSpeak) {
                handleTask();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void handleTask() throws IOException {
        FileWriter fstream = new FileWriter("out.txt");
        BufferedWriter out = new BufferedWriter(fstream);
        out.write(message + ": " + counter++ + " " + processName + "\n");
        out.close();
    }

    @Override
    public void startSpeaking() {
        this.canSpeak = true;
    }

    @Override
    public void stopSpeaking() {
        this.canSpeak = false;
    }

    @Override
    public String getProcessName() {
        return processName;
    }
}