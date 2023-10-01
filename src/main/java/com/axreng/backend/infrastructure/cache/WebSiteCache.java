package com.axreng.backend.infrastructure.cache;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class WebSiteCache {

    private static final ConcurrentHashMap<String, List<String>>  siteListCached = new ConcurrentHashMap<>();

    private static final WebSiteCache instance = new WebSiteCache();
    private WebSiteCache() {
    }

    public static WebSiteCache getInstance() {
        return instance;
    }

    public void put(String key, List<String> value) {
        siteListCached.put(key, value);
    }

    public List<String> get(String key) {
        return siteListCached.get(key);
    }

    public Boolean containsKey(String key) {
        return siteListCached.containsKey(key);
    }
}
