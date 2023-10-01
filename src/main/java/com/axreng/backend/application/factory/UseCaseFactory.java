package com.axreng.backend.application.factory;

import com.axreng.backend.application.usecases.AddNewSearchTermUseCase;
import com.axreng.backend.application.usecases.GetSearchTermResults;
import com.axreng.backend.application.usecases.ScrapeTermUseCase;
import com.axreng.backend.infrastructure.threads.ThreadPoolService;

import java.util.concurrent.ExecutorService;

public class UseCaseFactory {

    private final RepositoryFactory repositoryFactory;

    public UseCaseFactory(RepositoryFactory repositoryFactory) {
        this.repositoryFactory = repositoryFactory;
    }

    public AddNewSearchTermUseCase createAddNewSearchTermUseCase() {
        return new AddNewSearchTermUseCase(repositoryFactory.createSearchTermRepository());
    }

    public GetSearchTermResults createGetSearchTermResultsUseCase() {
        return new GetSearchTermResults(repositoryFactory.createSearchTermRepository());
    }

    public ScrapeTermUseCase createScrapeTermUseCase() {
        return new ScrapeTermUseCase(repositoryFactory.createSearchTermRepository(), ThreadPoolService.getInstance());
    }
}