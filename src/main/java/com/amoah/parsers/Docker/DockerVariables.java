package com.amoah.parsers.Docker;

import java.util.Map;

import lombok.Builder;

@Builder
public record DockerVariables(Map<String, String> envs, String expose, String volume, String workdir) {

}
