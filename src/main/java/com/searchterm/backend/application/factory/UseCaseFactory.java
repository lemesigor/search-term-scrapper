package com.searchterm.backend.application.factory;

import com.searchterm.backend.application.usecases.AddNewSearchTermUseCase;
import com.searchterm.backend.application.usecases.GetSearchTermResultsUseCase;
import com.searchterm.backend.application.usecases.ScrapeTermUseCase;
import com.searchterm.backend.infrastructure.cache.SiteMapCache;
import com.searchterm.backend.infrastructure.parser.HtmlParser;
import com.searchterm.backend.infrastructure.threads.TaskQueue;

public class UseCaseFactory {

    private final RepositoryFactory repositoryFactory;

    private final TaskQueue taskQueue;

    private final HtmlParser htmlParser;

    private final SiteMapCache siteMapCache;

    public UseCaseFactory(RepositoryFactory repositoryFactory, TaskQueue taskQueue, HtmlParser htmlParser, SiteMapCache siteMapCache) {
        this.repositoryFactory = repositoryFactory;
        this.taskQueue = taskQueue;
        this.htmlParser = htmlParser;
        this.siteMapCache = siteMapCache;
    }

    public AddNewSearchTermUseCase createAddNewSearchTermUseCase() {
        return new AddNewSearchTermUseCase(repositoryFactory.createSearchTermRepository());
    }

    public GetSearchTermResultsUseCase createGetSearchTermResultsUseCase() {
        return new GetSearchTermResultsUseCase(repositoryFactory.createSearchTermRepository());
    }

    public ScrapeTermUseCase createScrapeTermUseCase() {
        return new ScrapeTermUseCase(repositoryFactory.createSearchTermRepository(), taskQueue, htmlParser , siteMapCache);
    }
}