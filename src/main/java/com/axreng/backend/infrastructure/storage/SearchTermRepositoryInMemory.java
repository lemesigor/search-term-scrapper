package com.axreng.backend.infrastructure.storage;

import com.axreng.backend.application.domain.SearchTerm;
import com.axreng.backend.application.domain.Url;

import java.util.Map;
import java.util.Optional;

public class SearchTermRepositoryInMemory implements SearchTermRepository {

    Map<String, String> searchTermsRepository;
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
}
