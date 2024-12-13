package com.searchterm.backend.infrastructure.database;

import com.searchterm.backend.application.factory.RepositoryFactory;
import com.searchterm.backend.infrastructure.storage.SearchTermRepository;
import com.searchterm.backend.infrastructure.storage.SearchTermRepositoryInMemory;

public class InMemoryDBFactory implements RepositoryFactory {
    @Override
    final public SearchTermRepository createSearchTermRepository() {
         return SearchTermRepositoryInMemory.getInstance();
    }
}
