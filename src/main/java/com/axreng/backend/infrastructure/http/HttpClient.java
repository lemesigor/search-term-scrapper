package com.axreng.backend.infrastructure.http;

public interface HttpClient <TRoute>{

    String getResource(String path, TRoute handler);
    String postResource(String path,TRoute handler);
}
