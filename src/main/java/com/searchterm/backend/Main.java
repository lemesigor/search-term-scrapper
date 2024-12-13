package com.searchterm.backend;

import com.searchterm.backend.application.factory.RepositoryFactory;
import com.searchterm.backend.application.factory.UseCaseFactory;
import com.searchterm.backend.infrastructure.cache.SiteMapCache;
import com.searchterm.backend.infrastructure.cache.SiteMapCacheInMemory;
import com.searchterm.backend.infrastructure.database.InMemoryDBFactory;
import com.searchterm.backend.infrastructure.http.HttpClient;
import com.searchterm.backend.infrastructure.parser.HtmlParser;
import com.searchterm.backend.infrastructure.parser.HtmlVanillaParser;
import com.searchterm.backend.infrastructure.threads.TaskQueue;
import com.searchterm.backend.infrastructure.threads.ThreadPoolService;
import com.searchterm.backend.presentation.web.SparkAdapter;
import com.searchterm.backend.presentation.web.controllers.SearchTermController;
import com.searchterm.backend.shared.EnvironmentVariables;
import spark.Route;

import java.net.MalformedURLException;

public class Main {


    public static void main(String[] args) throws MalformedURLException {
        var envManager = EnvironmentVariables.getInstance();

        String baseUrl = envManager.getBaseURL(System::getenv);

        HttpClient<Route> client = new SparkAdapter();
        TaskQueue taskQueue = ThreadPoolService.getInstance();
        HtmlParser htmlParser = new HtmlVanillaParser();
        SiteMapCache siteMapCache = SiteMapCacheInMemory.getInstance();

        RepositoryFactory inMemoryDBFactory = new InMemoryDBFactory();
        UseCaseFactory useCaseFactory = new UseCaseFactory(inMemoryDBFactory, taskQueue, htmlParser, siteMapCache);

        SearchTermController controller = new SearchTermController(client, baseUrl, useCaseFactory);

        controller.listen();
    }
}
