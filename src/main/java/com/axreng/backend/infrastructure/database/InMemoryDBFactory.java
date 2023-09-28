package com.axreng.backend.infrastructure.database;

import com.axreng.backend.application.factory.RepositoryFactory;
import com.axreng.backend.infrastructure.storage.SearchTermRepository;
import com.axreng.backend.infrastructure.storage.SearchTermRepositoryInMemory;

public class InMemoryDBFactory implements RepositoryFactory {
    @Override
    final public SearchTermRepository createSearchTermRepository() {
         return SearchTermRepositoryInMemory.getInstance();
    }
}
