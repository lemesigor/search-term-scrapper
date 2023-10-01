package com.axreng.backend.infrastructure.parser;

import java.text.Normalizer;

public class HtmlNormalizationHelper {
    private HtmlNormalizationHelper() {
    }


    public static String normalizeWhitespace(String html) {
        return html.replaceAll("\\s+", " ").trim();
    }

    public static String normalizeUnicodeCharacters(String html) {
        return Normalizer.normalize(html, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }

    public static String normalizeNonBreakingSpace(String html) {
        return html.replaceAll("&nbsp;", " ");
    }

    public static String normalizeHtml(String html) {
        html = normalizeWhitespace(html);
        html = normalizeUnicodeCharacters(html);
        html = normalizeNonBreakingSpace(html);
        return html;
    }
}
