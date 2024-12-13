package com.searchterm.backend.infrastructure.parser;

import java.io.IOException;
import java.util.List;

public interface HtmlParser {
    List<String> parseContentAsStringList(String urlString) throws IOException;

    Boolean hasKeywordInHtmlAsStringList(List<String> htmlAsList, String keywordRegexPattern );
}
