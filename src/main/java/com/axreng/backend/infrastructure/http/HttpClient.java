package com.axreng.backend.infrastructure.http;
import spark.Request;

import java.util.function.Function;

public interface HttpClient <TRoute>{

    String getResource(String path, TRoute handler);
    String postResource(String path,TRoute handler);
}
