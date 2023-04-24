package com.amoah.control;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.amoah.control.configuration.BoardConfiguration;
import com.amoah.control.configuration.ServiceConfiguration;

import lombok.Getter;

public class Board implements Runnable {

    @Getter
    Map<String, MSControl> services;

    public Board(BoardConfiguration config) {

        this.services = new HashMap<>();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            this.services.values().forEach(s -> {
                s.interrupt();
            });
        }));

        if (config.services() == null) {
            System.out.println("No services configured");
            System.exit(0);
        }
        config.services().entrySet().forEach(e -> {
            String id = e.getKey();
            ServiceConfiguration serviceConfig = e.getValue();
            try {
                addProcess(id, serviceConfig);
            } catch (IOException ex) {
                System.out.println("Error adding service " + id);
            }
        });

    }

    public void addProcess(String id, ServiceConfiguration configuration) throws IOException {
        services.put(id, new MSControl(id, configuration));
    }

    @Override
    public void run() {
        services.values().forEach(s -> {
            try {
                s.start();
            } catch (IOException | InterruptedException e) {
                System.out.println("Could not start service " + s.getId());
            }
        });

        initInputThread();
        initOutputThread();

    }

    private void initOutputThread() {

        new Thread(() -> {

            while (true) {
                try {
                    new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                } catch (InterruptedException | IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                // Print header row
                System.out.printf("%-20s %-20s %-20s %-20s %-4s \n", "Process", "PID", "Health Check", "PORT", "ENVS");
                System.out.println("-------------------------------------------------------");

                this.services.values().forEach(s -> {

                    System.out.printf("%-20s %-20s %-20s %-20s %-4d \n", s.getId(), s.getId(), s.isUp() ? "UP" : "DOWN",
                            s.getPort(), s.getEnv().size());
                });

                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void initInputThread() {
        Scanner scanner = new Scanner(System.in);

        new Thread(() -> {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                System.out.println(line);
            }
            scanner.close();

        }).start();
    }

}
