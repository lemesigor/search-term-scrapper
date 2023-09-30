package com.axreng.backend.infrastructure.storage;

import com.axreng.backend.application.domain.SearchTerm;

import java.util.*;

public class SearchTermRepositoryInMemory implements SearchTermRepository {

    Map<String,SearchTerm > searchTermsRepository = new HashMap<String, SearchTerm>();

    private static final SearchTermRepositoryInMemory instance = new SearchTermRepositoryInMemory();

    private SearchTermRepositoryInMemory() {}

    // Public method to access the singleton instance
    public static SearchTermRepositoryInMemory getInstance() {
        return instance;
    }
    @Override
        public String save(String word) {
            var searchTerm = new SearchTerm(word);

            searchTermsRepository.put(searchTerm.getId(), searchTerm);
            return searchTerm.getId();

        }

        @Override
        public Optional<SearchTerm> findById(String id) {
            if(searchTermsRepository.containsKey(id)){
                return Optional.of(searchTermsRepository.get(id));
            }
            return Optional.empty();
        }

    @Override
    public void update(SearchTerm searchTerm) {
        searchTermsRepository.put(searchTerm.getId(), searchTerm);
    }

    @Override
    public List<SearchTerm> findAll() {
        return new ArrayList<>(searchTermsRepository.values());
    }
}
