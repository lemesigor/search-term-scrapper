package com.axreng.backend.presentation.web.controllers;

import com.axreng.backend.infrastructure.http.HttpClient;
import spark.Route;

public class SearchTermController {

    private final HttpClient<Route> httpClient;

    public SearchTermController(HttpClient<Route> httpClient) {
        this.httpClient = httpClient;
    }

    public void listen() {


        httpClient.getResource("/crawl/:id", (req, res) ->
                "GET /crawl/" + req.params("id"));


        httpClient.postResource("/crawl", (req, res) ->
                "POST /crawl" + System.lineSeparator() + req.body());
    }
}
