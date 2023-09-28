package com.axreng.backend.infrastructure.storage;

import com.axreng.backend.application.domain.Url;

import java.util.Optional;

public interface UrlRepository {

    String save(String url);
    Optional<Url> findById(String id);

}
