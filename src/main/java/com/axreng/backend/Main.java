package com.axreng.backend;

import com.axreng.backend.infrastructure.http.HttpClient;
import com.axreng.backend.infrastructure.http.SparkAdapter;
import spark.Route;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {

        HttpClient<Route> client = new SparkAdapter();

        client.getResource("/crawl/:id", (req, res) ->
                "GET /crawl/" + req.params("id"));

        client.postResource("/crawl", (req, res) ->
                "POST /crawl" + System.lineSeparator() + req.body());
    }
}
