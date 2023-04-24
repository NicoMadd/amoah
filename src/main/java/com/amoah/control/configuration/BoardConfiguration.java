package com.amoah.control.configuration;

import java.util.Map;

public record BoardConfiguration(Map<String, ServiceConfiguration> services) {

}
