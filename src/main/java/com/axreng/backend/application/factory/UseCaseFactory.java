package com.axreng.backend.application.factory;

import com.axreng.backend.application.usecases.AddNewSearchTermUseCase;

public class UseCaseFactory {

    private final RepositoryFactory repositoryFactory;

    public UseCaseFactory(RepositoryFactory repositoryFactory) {
        this.repositoryFactory = repositoryFactory;
    }

    public AddNewSearchTermUseCase createAddNewSearchTermUseCase() {
        return new AddNewSearchTermUseCase(repositoryFactory.createSearchTermRepository());
    }
}
