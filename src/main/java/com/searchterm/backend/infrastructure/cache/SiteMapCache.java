package com.searchterm.backend.infrastructure.cache;

import java.util.List;

public interface SiteMapCache {
    void put(String key, List<String> value);

    List<String> get(String key);

    Boolean containsKey(String key);

}
