package com.axreng.backend;

import com.axreng.backend.application.factory.RepositoryFactory;
import com.axreng.backend.application.factory.UseCaseFactory;
import com.axreng.backend.infrastructure.database.InMemoryDBFactory;
import com.axreng.backend.infrastructure.http.HttpClient;
import com.axreng.backend.infrastructure.threads.TaskQueue;
import com.axreng.backend.infrastructure.threads.ThreadPoolService;
import com.axreng.backend.presentation.web.SparkAdapter;
import com.axreng.backend.presentation.web.controllers.SearchTermController;
import com.axreng.backend.shared.EnvironmentVariables;
import spark.Route;

import java.net.MalformedURLException;

public class Main {


    public static void main(String[] args) throws MalformedURLException {

        var envManager = EnvironmentVariables.getInstance();

        String baseUrl = envManager.getBaseURL();

        HttpClient<Route> client = new SparkAdapter();
        TaskQueue taskQueue = ThreadPoolService.getInstance();

        RepositoryFactory inMemoryDBFactory = new InMemoryDBFactory();
        UseCaseFactory useCaseFactory = new UseCaseFactory(inMemoryDBFactory, taskQueue);

        SearchTermController controller = new SearchTermController(client, baseUrl, useCaseFactory);

        controller.listen();

    }
}
