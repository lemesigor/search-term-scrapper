package com.searchterm.backend.infrastructure.cache;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SiteMapCacheInMemory implements SiteMapCache {

    private static final ConcurrentHashMap<String, List<String>>  siteListCached = new ConcurrentHashMap<>();

    private static final SiteMapCacheInMemory instance = new SiteMapCacheInMemory();
    private SiteMapCacheInMemory() {
    }

    public static SiteMapCacheInMemory getInstance() {
        return instance;
    }

    @Override
    public void put(String key, List<String> value) {
        siteListCached.put(key, value);
    }

    @Override
    public List<String> get(String key) {
        return siteListCached.get(key);
    }

    @Override
    public Boolean containsKey(String key) {
        return siteListCached.containsKey(key);
    }
}
