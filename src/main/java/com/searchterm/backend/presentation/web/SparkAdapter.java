package com.searchterm.backend.presentation.web;

import com.searchterm.backend.infrastructure.http.HttpClient;
import spark.*;

import static spark.Spark.*;

public class SparkAdapter implements HttpClient<Route> {
    @Override
    public String getResource(String path, Route handler) {
        get(path, handler);
        return null;
    }

    @Override
    public String postResource(String path, Route handler) {
        post(path, handler);
        return  null;
    }
}
