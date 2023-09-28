package com.axreng.backend;

import com.axreng.backend.infrastructure.http.HttpClient;
import com.axreng.backend.presentation.web.SparkAdapter;
import com.axreng.backend.presentation.web.controllers.SearchTermController;
import spark.Route;

public class Main {
    public static void main(String[] args) {

        HttpClient<Route> client = new SparkAdapter();

        SearchTermController controller = new SearchTermController(client);

        controller.listen();

    }
}
