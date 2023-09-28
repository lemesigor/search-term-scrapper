package com.axreng.backend.presentation.web.controllers;

import com.axreng.backend.application.factory.UseCaseFactory;
import com.axreng.backend.infrastructure.http.HttpClient;
import com.axreng.backend.presentation.web.controllers.requests.AddSearchTermDTO;
import com.axreng.backend.presentation.web.controllers.responses.DefaultErrorResponse;
import com.google.gson.Gson;
import org.eclipse.jetty.util.log.Slf4jLog;
import spark.Route;

public class SearchTermController {

    private final HttpClient<Route> httpClient;
    private final String baseUrl;

    private final UseCaseFactory useCaseFactory;
    private final String defaultResponseType = "application/json";

    public SearchTermController(HttpClient<Route> httpClient, String baseUrl, UseCaseFactory useCaseFactory) {
        this.httpClient = httpClient;
        this.baseUrl = baseUrl;
        this.useCaseFactory = useCaseFactory;
    }

    public void listen() {

        var logger = new Slf4jLog("SearchTermController");
        logger.info("Listening on /crawl route 4567");


        httpClient.getResource("/crawl/:id", (request, response) ->
                "GET /crawl/" + request.params("id"));

        httpClient.getResource("/crawl", (request, response) ->((useCaseFactory.createAddNewSearchTermUseCase().getAllSearchTerms())));

        httpClient.postResource("/crawl", (request, response) -> {
            response.type(defaultResponseType);

            try{
                var wordRequest = new Gson().fromJson(request.body(), AddSearchTermDTO.class);
                var responseFromUseCase = useCaseFactory.createAddNewSearchTermUseCase().execute(wordRequest.getKeyword());

                logger.info("POST /crawl " + wordRequest.getKeyword());

                return new Gson().toJson(responseFromUseCase);

            } catch (Exception e) {
                var defaultErrorResponse = new DefaultErrorResponse(e.getMessage(), e);
                response.status(defaultErrorResponse.getStatus());
                return new Gson().toJson(defaultErrorResponse);
            }
        });
    }
}
