package com.axreng.backend.integration.presentation.web;

import com.axreng.backend.Main;
import com.axreng.backend.application.domain.SearchStatus;
import com.axreng.backend.integration.presentation.web.utils.HttpClientUtils;
import com.axreng.backend.integration.presentation.web.utils.TestResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import spark.Spark;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WebTest {

    private final ArrayList<String> ids = new ArrayList<>();

    @BeforeAll
    static void setUp() throws MalformedURLException {
        Main.main(null);
        Spark.awaitInitialization();
    }

    @AfterAll
    public static void tearDown() {
        System.out.println("Stopping Spark");
        Spark.stop();
        Spark.awaitStop();
    }


    @Test
    public void shouldStartScrappingAfterValidWordIsSentAndGetResults() throws IOException {
        String bodyJson = "{\n" +
                "  \"keyword\": \"happy\"\n" +
                "}";

        TestResponse response = HttpClientUtils.request("POST", "/crawl", bodyJson);
        assertEquals(200, response.status);
        Map<String, String> json = response.json();

        assertTrue(json.containsKey("id"));

        ids.add(json.get("id"));

        response = HttpClientUtils.request("GET", "/crawl/" + ids.get(0), null);

        assertEquals(200, response.status);

        json = response.json();

        assertTrue(json.containsKey("status"));
        assertEquals(json.get("id"), ids.get(0));

        assertEquals(SearchStatus.active.toString(), json.get("status"));

        assertTrue(json.containsKey("urls"));


    }

    @Test
    public void shouldBeAbleToScrapMoreThanKeywordAtTime() throws IOException {
        String bodyJson = "{\n" +
                "  \"keyword\": \"happy\"\n" +
                "}";

        TestResponse response = HttpClientUtils.request("POST", "/crawl", bodyJson);
        assertEquals(200, response.status);
        Map<String, String> json = response.json();

        assertTrue(json.containsKey("id"));

        ids.add(json.get("id"));

        bodyJson = "{\n" +
                "  \"keyword\": \"mouse\"\n" +
                "}";

        response = HttpClientUtils.request("POST", "/crawl", bodyJson);
        assertEquals(200, response.status);
        json = response.json();

        assertTrue(json.containsKey("id"));

        ids.add(json.get("id"));

    }
}
