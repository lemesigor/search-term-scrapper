package com.axreng.backend.presentation.web.controllers;

import com.axreng.backend.infrastructure.http.HttpClient;
import com.axreng.backend.presentation.web.controllers.requests.AddSearchTermDTO;
import com.google.gson.Gson;
import org.eclipse.jetty.util.log.Slf4jLog;
import spark.Route;

public class SearchTermController {

    private final HttpClient<Route> httpClient;
    private final String baseUrl;

    private final String defaultResponseType = "application/json";

    public SearchTermController(HttpClient<Route> httpClient, String baseUrl) {
        this.httpClient = httpClient;
        this.baseUrl = baseUrl;
    }

    public void listen() {

        var logger = new Slf4jLog("SearchTermController");
        logger.info("Listening on /crawl route 4567");


        httpClient.getResource("/crawl/:id", (request, response) ->
                "GET /crawl/" + request.params("id"));


        httpClient.postResource("/crawl", (request, response) -> {
            response.type(defaultResponseType);
            var wordRequest = new Gson().fromJson(request.body(), AddSearchTermDTO.class);
            return new Gson().toJson(wordRequest);

        });
    }
}
