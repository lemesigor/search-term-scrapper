package com.axreng.backend.unit.infrastructure;

import com.axreng.backend.infrastructure.parser.HtmlParser;
import com.axreng.backend.infrastructure.parser.HtmlVanillaParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;


import java.util.Arrays;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {



    private HtmlParser htmlParser;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        htmlParser = new HtmlVanillaParser();
    }


    @Test
    void hasKeywordInHtmlAsStringListShouldReturnTrueIfKeywordExists() {
        List<String> htmlAsList = Arrays.asList("<html>", "<head>", "<title>Keyword</title>", "</head>", "<body>", "</body>", "</html>");
        String keywordRegexPattern = "Keyword";

        assertTrue(htmlParser.hasKeywordInHtmlAsStringList(htmlAsList, keywordRegexPattern));
    }

    @Test
    void hasKeywordInHtmlAsStringListShouldReturnFalseIfKeywordDoesNotExist() {
        List<String> htmlAsList = Arrays.asList("<html>", "<head>", "<title>Keyword</title>", "</head>", "<body>", "</body>", "</html>");
        String keywordRegexPattern = "notKeyword";

        assertFalse(htmlParser.hasKeywordInHtmlAsStringList(htmlAsList, keywordRegexPattern));
    }
}
