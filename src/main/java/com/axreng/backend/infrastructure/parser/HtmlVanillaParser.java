package com.axreng.backend.infrastructure.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HtmlVanillaParser implements HtmlParser {

    @Override
    public List<String> parseContentAsStringList(String urlString) throws IOException {
        try {
            URL url = new URL(urlString);
            BufferedReader htmlLines = new BufferedReader(new InputStreamReader(url.openStream()));
            Stream<String> lines = htmlLines.lines();

            var linesList = lines.parallel()
                    .map(HtmlNormalizationHelper::normalizeHtml)
                    .collect(Collectors.toList());

            htmlLines.close();

            return linesList;
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    @Override
    public Boolean hasKeywordInHtmlAsStringList(List<String> htmlAsList, String keywordRegexPattern) {
        Pattern patternToFound = Pattern.compile(keywordRegexPattern, Pattern.CASE_INSENSITIVE);
        return htmlAsList.stream().anyMatch(line -> patternToFound.matcher(line).find());

    }
}
