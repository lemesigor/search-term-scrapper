package com.axreng.backend.application.usecases;

import com.axreng.backend.application.domain.SearchTerm;
import com.axreng.backend.application.domain.TermScraper;
import com.axreng.backend.infrastructure.storage.SearchTermRepository;

import java.util.List;

public class AddNewSearchTermUseCase {

    private final SearchTermRepository repository;

    public AddNewSearchTermUseCase(SearchTermRepository repository) {
        this.repository = repository;



    }

    public String execute(String url) {
        TermScraper termScraper = new TermScraper(System.getenv("BASE_URL"), url);

        termScraper.execute();
        return repository.save(url);
    }

    public List<SearchTerm> getAllSearchTerms() {
        return repository.findAll();
    }
}
