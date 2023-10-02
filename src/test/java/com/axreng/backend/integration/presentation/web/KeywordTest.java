package com.axreng.backend.integration.presentation.web;

import com.axreng.backend.Main;
import com.axreng.backend.integration.presentation.web.utils.HttpClientUtils;
import com.axreng.backend.integration.presentation.web.utils.TestResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import spark.Spark;

import java.io.IOException;
import java.net.MalformedURLException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class KeywordTest {


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
    public void shouldReceiveANewSearchValidKeywordAndReturnId() throws IOException {
        String bodyJson = "{\n" +
                "  \"keyword\": \"handy\"\n" +
                "}";
        TestResponse response = HttpClientUtils.request("POST","/crawl", bodyJson);
        assertEquals(200, response.status);
        Map<String, String> json = response.json();
        assertTrue(json.containsKey("id"));
    }

    @Test
    public void shouldRejectInvalidKeyword()  {
        String bodyJson = "{\n" +
                "  \"keyword\": \"han\"\n" +
                "}";
        assertThrows(IOException.class, () -> HttpClientUtils.request("POST", "/crawl", bodyJson));
    }

    @Test
    public void shouldBeAbleToRetrieveStatusOfKeyword() throws IOException {
        String bodyJson = "{\n" +
                "  \"keyword\": \"handy\"\n" +
                "}";
        TestResponse response = HttpClientUtils.request("POST", "/crawl", bodyJson);

        assertEquals(200, response.status);
        Map<String, String> json = response.json();
        assertTrue(json.containsKey("id"));
        String id = json.get("id");


        response = HttpClientUtils.request("GET", "/crawl/" + id, null);

        assertEquals(200, response.status);
        json = response.json();
        assertTrue(json.containsKey("status"));
        assertEquals("active", json.get("status"));
    }


}
