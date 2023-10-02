package com.axreng.backend.integration.presentation.web.utils;

import spark.utils.IOUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClientUtils {

    public static TestResponse request(String method, String path, String requestBody) throws IOException {
        URL url = new URL("http://localhost:4567" + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setDoOutput(true);

        if (requestBody != null) {
            connection.getOutputStream().write(requestBody.getBytes());
        }

        connection.connect();
        String body = IOUtils.toString(connection.getInputStream());
        return new TestResponse(connection.getResponseCode(), body);
    }

}
