package com.axreng.backend;

import com.axreng.backend.infrastructure.http.HttpClient;
import com.axreng.backend.presentation.web.SparkAdapter;
import com.axreng.backend.presentation.web.controllers.SearchTermController;
import spark.Route;

public class Main {
    public static void main(String[] args) {

        String baseUrl = System.getenv("BASE_URL");

        if (baseUrl == null) {
            throw new RuntimeException("BASE_URL environment variable not set");
        }

        HttpClient<Route> client = new SparkAdapter();

        SearchTermController controller = new SearchTermController(client, baseUrl);

        controller.listen();

    }
}
