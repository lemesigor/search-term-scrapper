package com.axreng.backend.infrastructure.storage;

import com.axreng.backend.application.domain.SearchTerm;

import java.util.List;
import java.util.Optional;

public interface SearchTermRepository {

    String save(String word);
    Optional<SearchTerm> findById(String id);

    List<SearchTerm> findAll();


    void update(SearchTerm searchTerm);

}
