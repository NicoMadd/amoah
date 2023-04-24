package com.amoah.control.configuration;

import java.util.HashMap;
import java.util.Map;

public record ServiceConfiguration(String path, Integer port, String logDir, Map<String, String> env) {

    public ServiceConfiguration() {
        this(null, 8080, "logs", new HashMap<>());
    }
}