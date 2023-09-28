package com.axreng.backend.infrastructure.storage;

import com.axreng.backend.application.domain.SearchTerm;
import com.axreng.backend.application.domain.Url;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SearchTermRepositoryInMemory implements SearchTermRepository {

    Map<String, String> searchTermsRepository = new HashMap<String, String>();

    private static final SearchTermRepositoryInMemory instance = new SearchTermRepositoryInMemory();

    private SearchTermRepositoryInMemory() {}

    // Public method to access the singleton instance
    public static SearchTermRepositoryInMemory getInstance() {
        return instance;
    }
    @Override
        public String save(String word) {
            var searchTerm = new SearchTerm(word);

            searchTermsRepository.put(searchTerm.getId(), searchTerm.getWord());
            return searchTerm.getId();

        }

        @Override
        public Optional<SearchTerm> findById(String id) {
            if(searchTermsRepository.containsKey(id)){
                return Optional.of(new SearchTerm(searchTermsRepository.get(id)));
            }
            return Optional.empty();
        }

    @Override
    public List<SearchTerm> findAll() {
        return searchTermsRepository.values().stream().map(SearchTerm::new).collect(Collectors.toList());
    }
}
