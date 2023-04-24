package com.amoah;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import com.amoah.control.Board;
import com.amoah.control.configuration.BoardConfiguration;
import com.amoah.parsers.Docker.DockerParser;
import com.google.gson.GsonBuilder;

public class App {

    static Thread mainBoardThread;

    private static void mainBoard(String configPath) {
        System.out.println("Opening Configuration path: " + configPath);
        File f = new File(configPath);

        if (!f.exists()) {
            throw new RuntimeException("Configuration file does not exist");
        }

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {

            BoardConfiguration config = new GsonBuilder().create().fromJson(br, BoardConfiguration.class);

            // boardMainThread = new Thread("main-board").;

            Board board = new Board(config);

            mainBoardThread = new Thread(board);
            mainBoardThread.start();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private static String validateArg(String[] args, int i) {
        if (args.length >= i) {
            return args[i];
        } else {
            throw new RuntimeException("Invalid no argument " + i);
        }

    }

    private static void readDocker(String dockerfilePath) throws IOException {
        // open file with a file class
        System.out.println("Opening Dockerfile: " + dockerfilePath);
        File f = new File(dockerfilePath);

        if (!f.exists()) {
            throw new RuntimeException("Dockerfile does not exist");
        }
        DockerParser.parse(f);

    }

    public static void main(String[] args) throws IOException, InterruptedException {

        try {
            String mainMethod;
            try {
                mainMethod = validateArg(args, 0);

            } catch (Exception e) {
                mainMethod = "control";
            }

            switch (mainMethod) {
                case "docker":
                    try {
                        String validateFilepath = validateArg(args, 1);
                        readDocker(validateFilepath);
                    } catch (Exception e) {
                        System.out.println("Defaulting filepath to 'Dockerfile' in the current directory");
                        readDocker("./Dockerfile");
                    }
                    break;
                case "control":
                default:
                    try {
                        String validateConfpath = validateArg(args, 1);
                        mainBoard(validateConfpath);
                    } catch (Exception e) {
                        System.out.println("Defaulting filepath to 'config.json' in the current directory");
                        mainBoard("./config.json");
                    }
            }

            mainBoardThread.join();

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }
}
