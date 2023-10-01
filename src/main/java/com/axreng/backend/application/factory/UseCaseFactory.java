package com.axreng.backend.application.factory;

import com.axreng.backend.application.usecases.AddNewSearchTermUseCase;
import com.axreng.backend.application.usecases.GetSearchTermResultsUseCase;
import com.axreng.backend.application.usecases.ScrapeTermUseCase;
import com.axreng.backend.infrastructure.threads.TaskQueue;
import com.axreng.backend.infrastructure.threads.ThreadPoolService;

public class UseCaseFactory {

    private final RepositoryFactory repositoryFactory;

    private final TaskQueue taskQueue;

    public UseCaseFactory(RepositoryFactory repositoryFactory, TaskQueue taskQueue) {
        this.repositoryFactory = repositoryFactory;
        this.taskQueue = taskQueue;
    }

    public AddNewSearchTermUseCase createAddNewSearchTermUseCase() {
        return new AddNewSearchTermUseCase(repositoryFactory.createSearchTermRepository());
    }

    public GetSearchTermResultsUseCase createGetSearchTermResultsUseCase() {
        return new GetSearchTermResultsUseCase(repositoryFactory.createSearchTermRepository());
    }

    public ScrapeTermUseCase createScrapeTermUseCase() {
        return new ScrapeTermUseCase(repositoryFactory.createSearchTermRepository(), taskQueue);
    }
}