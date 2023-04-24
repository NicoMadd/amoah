package com.amoah.parsers.Docker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amoah.parsers.ParseBlock;

public class DockerParser {

    private static final String ENV_PATTERN = "ENV (.+)=(.+)";
    private static final String PORT_PATTERN = "EXPOSE\\s+(\\d+)";
    private static final String CMD_PATTERN = "CMD\\s+(.+)";
    private static final String COPY_PATTERN = "COPY\\s+(.+)\\s+(.+)";
    private static final String WORKDIR_PATTERN = "WORKDIR\\s+(.+)";
    private static final String ARG_PATTERN = "ARG (.+)=(.+)";

    public static Map<String, Object> parse(File dockerfile) {
        Map<String, Object> result = new HashMap<>();
        Map<String, String> envVars = new HashMap<>();
        Map<String, String> argsVars = new HashMap<>();
        int port = -1;
        String cmd = null;
        String copyFrom = null;
        String copyTo = null;
        String workDir = null;

        // DockerVariablesBuilder builder = DockerVariables.builder();

        try (BufferedReader reader = new BufferedReader(new FileReader(dockerfile))) {

            String line;
            ParseBlock parseBlock;
            while ((line = reader.readLine()) != null) {
                parseBlock = new ParseBlock(ENV_PATTERN, 2);
                if (parseBlock.matches(line)) {
                    String[] parse = parseBlock.parse(line);
                    envVars.put(parse[0], parse[1]);
                }
                parseBlock = new ParseBlock(ARG_PATTERN, 2);
                if (parseBlock.matches(line)) {
                    String[] parse = parseBlock.parse(line);
                    argsVars.put(parse[0], parse[1]);
                }

                Matcher portMatcher = Pattern.compile(PORT_PATTERN).matcher(line);
                if (portMatcher.matches()) {
                    port = Integer.parseInt(portMatcher.group(1));
                }
                Matcher cmdMatcher = Pattern.compile(CMD_PATTERN).matcher(line);
                if (cmdMatcher.matches()) {
                    cmd = cmdMatcher.group(1);
                }
                Matcher copyMatcher = Pattern.compile(COPY_PATTERN).matcher(line);
                if (copyMatcher.matches()) {
                    copyFrom = copyMatcher.group(1);
                    copyTo = copyMatcher.group(2);
                }
                Matcher workDirMatcher = Pattern.compile(WORKDIR_PATTERN).matcher(line);
                if (workDirMatcher.matches()) {
                    workDir = workDirMatcher.group(1);
                }
            }
            reader.close();
        } catch (NumberFormatException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        result.put("envVars", envVars);
        result.put("port", port);
        result.put("cmd", cmd);
        result.put("copyFrom", copyFrom);
        result.put("copyTo", copyTo);
        result.put("workDir", workDir);
        result.put("argsVars", argsVars);

        System.out.println(result.toString());
        return result;

    }

}
