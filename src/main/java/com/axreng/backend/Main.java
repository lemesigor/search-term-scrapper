package com.axreng.backend;

import com.axreng.backend.application.factory.RepositoryFactory;
import com.axreng.backend.application.factory.UseCaseFactory;
import com.axreng.backend.infrastructure.database.InMemoryDBFactory;
import com.axreng.backend.infrastructure.http.HttpClient;
import com.axreng.backend.infrastructure.threads.TaskQueue;
import com.axreng.backend.infrastructure.threads.ThreadPoolService;
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
        TaskQueue taskQueue = ThreadPoolService.getInstance();

        RepositoryFactory inMemoryDBFactory = new InMemoryDBFactory();
        UseCaseFactory useCaseFactory = new UseCaseFactory(inMemoryDBFactory, taskQueue);

        SearchTermController controller = new SearchTermController(client, baseUrl, useCaseFactory);

        controller.listen();

    }
}
