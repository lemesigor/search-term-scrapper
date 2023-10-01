package com.axreng.backend.infrastructure.parser;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public interface HtmlParser {
    List<String> parseContentAsStringList(URL url) throws IOException;

    Boolean hasKeywordInHtmlAsStringList(List<String> htmlAsList, String keywordRegexPattern );
}
