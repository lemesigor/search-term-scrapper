package com.searchterm.backend.application.factory;

import com.searchterm.backend.infrastructure.storage.SearchTermRepository;

public interface RepositoryFactory {
    SearchTermRepository createSearchTermRepository();
}
