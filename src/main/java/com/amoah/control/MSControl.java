package com.amoah.control;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.amoah.control.configuration.ServiceConfiguration;

import lombok.Getter;

public class MSControl implements Runnable {
    private Process process;

    @Getter
    private Map<String, String> env;

    @Getter
    private String id;

    @Getter
    private String path;

    @Getter
    private boolean up;

    @Getter
    private int port;

    private File logFile;

    public MSControl(String id, ServiceConfiguration configuration) throws IOException {
        this.id = id;
        this.path = configuration.path();
        this.port = configuration.port();
        this.env = configuration.env() == null ? new HashMap<>() : configuration.env();
        initLog(configuration.logDir());
    }

    private void initLog(String logDir) throws IOException {
        File dir = new File(logDir);
        dir.mkdirs();
        this.logFile = new File(logDir + File.separator + id + ".log");
        if (!logFile.exists())
            logFile.createNewFile();

    }

    public void start() throws IOException, InterruptedException {

        System.out.println("Starting " + id);
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", path);
        processBuilder.redirectErrorStream(true); // Merge the process's error stream
        processBuilder.redirectOutput(logFile);
        processBuilder.environment().putAll(env);
        this.process = processBuilder.start();
        this.up = true;
        initOutputThread(this.process);
        initHealthCheckThread(this.process);

    }

    private void initHealthCheckThread(Process process) {
        new Thread(() -> {

            while (true) {
                try {
                    Thread.sleep(1000);
                    this.up = this.process.isAlive();

                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    this.up = false;
                }

            }

        }).start();
    }

    private void initOutputThread(Process process) {
        new Thread(() -> {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    BufferedWriter logWriter = new BufferedWriter(new FileWriter(logFile))) {

                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    System.out.println(line);
                    logWriter.append(line + "\n");
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }).start();
    }

    public void interrupt() {
        System.out.println("Interrupting " + id);
        this.process.destroy();
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        try {
            start();
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
