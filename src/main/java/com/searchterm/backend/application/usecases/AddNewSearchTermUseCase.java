package com.searchterm.backend.application.usecases;

import com.searchterm.backend.application.domain.SearchTerm;
import com.searchterm.backend.infrastructure.storage.SearchTermRepository;

import java.util.List;

public class AddNewSearchTermUseCase {

    private final SearchTermRepository repository;

    public AddNewSearchTermUseCase(SearchTermRepository repository) {
        this.repository = repository;

    }

    public String execute(String word) {
        return repository.save(word);
    }

    public List<SearchTerm> getAllSearchTerms() {
        return repository.findAll();
    }
}
