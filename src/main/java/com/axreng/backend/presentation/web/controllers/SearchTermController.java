package com.axreng.backend.presentation.web.controllers;

import com.axreng.backend.application.factory.UseCaseFactory;
import com.axreng.backend.infrastructure.http.HttpClient;
import com.axreng.backend.presentation.web.controllers.requests.AddSearchTermDTO;
import com.axreng.backend.presentation.web.controllers.responses.AddSearchTermResponseDTO;
import com.axreng.backend.presentation.web.controllers.responses.DefaultErrorResponse;
import com.axreng.backend.presentation.web.controllers.responses.SearchTermsResultsResponseDTO;
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


        httpClient.getResource("/crawl/:id", (request, response) -> {
            response.type(defaultResponseType);
            var id = request.params(":id");

            logger.info("GET /crawl/" + id);

            try {
                var searchTermResults = useCaseFactory.createGetSearchTermResultsUseCase().execute(id);

                if (searchTermResults.isEmpty()) {
                    response.status(404);
                    var defaultErrorResponse = new DefaultErrorResponse("crawl not found: " + id, null);
                    defaultErrorResponse.setStatus(response.status());
                    return new Gson().toJson(defaultErrorResponse);
                }


                return new Gson().toJson(SearchTermsResultsResponseDTO.fromDomain(searchTermResults.get()));

            } catch (Exception e) {
                var defaultErrorResponse = new DefaultErrorResponse(e.getMessage(), e);
                response.status(defaultErrorResponse.getStatus());
                return new Gson().toJson(defaultErrorResponse);
            }

        });

        httpClient.getResource("/crawl", (request, response) -> ((useCaseFactory.createAddNewSearchTermUseCase().getAllSearchTerms())));

        httpClient.postResource("/crawl", (request, response) -> {
            response.type(defaultResponseType);

            try {
                var wordRequest = new Gson().fromJson(request.body(), AddSearchTermDTO.class);

                logger.info("POST /crawl " + wordRequest.getKeyword());

                var wordIdAfterCreated = useCaseFactory.createAddNewSearchTermUseCase().execute(wordRequest.getKeyword());

                var startScrapeUseCase = useCaseFactory.createScrapeTermUseCase();
                startScrapeUseCase.execute(wordIdAfterCreated, baseUrl);

                logger.info("Scrape started");

                return new Gson().toJson(new AddSearchTermResponseDTO(wordIdAfterCreated));

            } catch (Exception e) {
                var defaultErrorResponse = new DefaultErrorResponse(e.getMessage(), e);
                response.status(defaultErrorResponse.getStatus());
                return new Gson().toJson(defaultErrorResponse);
            }
        });
    }
}
