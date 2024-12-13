package com.searchterm.backend.application.usecases;

import com.searchterm.backend.application.domain.SearchTerm;
import com.searchterm.backend.infrastructure.storage.SearchTermRepository;

import java.util.Optional;

public class GetSearchTermResultsUseCase {
    private final SearchTermRepository repository;

    public GetSearchTermResultsUseCase(SearchTermRepository repository) {
        this.repository = repository;
    }

    public Optional<SearchTerm> execute(String id) {
        return repository.findById(id);
    }
}