package com.amoah.parsers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record ParseBlock(String pattern, Integer extractNGroups) {

    public String[] parse(String text) {
        Matcher envMatcher = Pattern.compile(pattern).matcher(text);
        String[] envVars = new String[extractNGroups];
        if (envMatcher.matches()) {
            for (int i = 1; i <= extractNGroups; i++) {
                envVars[i - 1] = envMatcher.group(i);

            }
        }
        return envVars;
    }

    public boolean matches(String text) {
        return Pattern.compile(pattern).matcher(text).matches();
    }
}
