package com.axreng.backend.shared;

import java.net.MalformedURLException;
import java.net.URL;

public class EnvironmentVariables {
    private final static EnvironmentVariables instance = new EnvironmentVariables();

    private final static String BASE_URL = "BASE_URL";

    public static EnvironmentVariables getInstance() {
        return instance;
    }

    private EnvironmentVariables() {
    }

    public String getBaseURL() throws MalformedURLException {
        String url = System.getenv(BASE_URL);

        if (url == null) {
            throw new RuntimeException("BASE_URL environment variable not set, please set it");
        }

        if (!url.endsWith("/")) {
            url = url + "/";
        }

        try {
            new URL(url);
            return url;
        } catch (MalformedURLException e) {
            throw new MalformedURLException("BASE_URL environment variable is not a valid URL, please check it");
        }
    }
}

