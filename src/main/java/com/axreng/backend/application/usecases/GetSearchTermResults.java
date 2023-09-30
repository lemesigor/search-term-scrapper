package com.axreng.backend.application.usecases;

import com.axreng.backend.application.domain.SearchTerm;
import com.axreng.backend.infrastructure.storage.SearchTermRepository;

import java.util.Optional;

public class GetSearchTermResults {
    private final SearchTermRepository repository;

    public GetSearchTermResults(SearchTermRepository repository) {
        this.repository = repository;
    }

    public Optional<SearchTerm> execute(String id) {
        return repository.findById(id);
    }
}