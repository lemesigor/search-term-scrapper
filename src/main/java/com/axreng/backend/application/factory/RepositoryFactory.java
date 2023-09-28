package com.axreng.backend.application.factory;

import com.axreng.backend.infrastructure.storage.SearchTermRepository;

public interface RepositoryFactory {
    SearchTermRepository createSearchTermRepository();
}
