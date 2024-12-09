package com.axreng.backend.shared;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Function;

public class EnvironmentVariables {
    private final static EnvironmentVariables instance = new EnvironmentVariables();

    private final static String BASE_URL = "BASE_URL";
    public final static String DEFAULT_URL = "";

    private final Logger logger = LoggerFactory.getLogger(EnvironmentVariables.class);
    public static EnvironmentVariables getInstance() {
        return instance;
    }

    private EnvironmentVariables() {
    }

    public String getBaseURL(Function<String,String> envGetter) throws MalformedURLException {
        String url = envGetter.apply(BASE_URL);

        if (url == null) {
            logger.info("BASE_URL environment variable not set, using the default one");
            url = DEFAULT_URL;
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

