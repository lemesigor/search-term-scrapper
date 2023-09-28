package com.axreng.backend.infrastructure.storage;

import com.axreng.backend.application.domain.Url;

import java.util.Map;
import java.util.Optional;

public class UrlRepositoryInMemory implements UrlRepository {

    Map<String, String> urlsRepository;
        @Override
        public String save(String url) {
            Url urlObject = new Url(url);

            urlsRepository.put(urlObject.getId(), urlObject.getUrl());
            return urlObject.getId();

        }

        @Override
        public Optional<Url> findById(String id) {
            if(urlsRepository.containsKey(id)){
                return Optional.of(new Url(urlsRepository.get(id)));
            }
            return Optional.empty();
        }
}
