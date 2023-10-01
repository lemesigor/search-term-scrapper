package com.axreng.backend.application.usecases;

import com.axreng.backend.application.domain.SearchStatus;
import com.axreng.backend.application.domain.SearchTerm;
import com.axreng.backend.infrastructure.parser.HtmlParser;
import com.axreng.backend.infrastructure.storage.SearchTermRepository;
import com.axreng.backend.infrastructure.threads.TaskQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ScrapeTermUseCase {


    private String url;

    private final Set<String> visitedUrls;
    private final Set<String> foundUrls;

    private final Logger logger = LoggerFactory.getLogger(ScrapeTermUseCase.class);

    private final TaskQueue poolService;


    private final SearchTermRepository repository;
    private final AtomicInteger totalUrlsToVisit;
    private final AtomicInteger visitedUrlsCount;
    private static final String HTML_EXTENSION = ".html";
    private static final String MAILTO_PREFIX = "mailto";

    private static final String ANCHOR_LINK_REGEX = "href=\"(.*?)\"";

    private SearchTerm temporarySearchTerm;

    private final HtmlParser htmlParser;

    public ScrapeTermUseCase(SearchTermRepository repository, TaskQueue poolService, HtmlParser htmlParser) {
        this.repository = repository;
        this.visitedUrls = new HashSet<>();
        this.foundUrls = new HashSet<>();
        this.totalUrlsToVisit = new AtomicInteger(1);
        this.visitedUrlsCount = new AtomicInteger(0);
        this.poolService = poolService;
        this.htmlParser = htmlParser;
    }

    public void execute(String termId, String baseUrl) {
        try {
            this.temporarySearchTerm = repository.findById(termId)
                    .orElseThrow(() -> new NoSuchElementException("termId not found"));
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage());
            return;
        }

        this.url = baseUrl;

        String wordRegexPattern = "\\b" + this.temporarySearchTerm.getWord() + "\\b";

        logger.info("Starting scraper for url: " + this.url + " and term: " + this.temporarySearchTerm.getWord());

        CompletableFuture.runAsync(() -> poolService.addTask(() -> scrape(this.url, wordRegexPattern)), poolService.getExecutor());
    }


    private void scrape(String currentUrl, String term) {

        try {
            final URL urlObj = new URL(currentUrl);

            List<String> htmlContentLinesList = this.htmlParser.parseContentAsStringList(urlObj);

            if (this.htmlParser.hasKeywordInHtmlAsStringList(htmlContentLinesList, term)) {
                this.temporarySearchTerm.addUrl(currentUrl);
                updateSearchTermFoundUrls(this.temporarySearchTerm);
            }

            Map<String, URL> internalUrls = findInternalUrlsForCurrentPage(htmlContentLinesList, currentUrl);

            for (URL internalUrl : internalUrls.values()) {
                String newUrl = internalUrl.toString();

                if (!this.isUrlAlreadyVisited(newUrl)) {
                    totalUrlsToVisit.incrementAndGet();
                    poolService.addTask(() -> scrape(newUrl, term));
                    foundUrls.add(newUrl);
                }
            }

            visitedUrls.add(currentUrl);
            visitedUrlsCount.incrementAndGet();

            if (isFinalThreadInteraction()) {
                logger.info("result set final de " + term + " " + this.temporarySearchTerm.getUrls());

                updateSearchTermStatusToDone(this.temporarySearchTerm);

            }

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private Map<String, URL> findInternalUrlsForCurrentPage(List<String> lines, String actualUrl) throws MalformedURLException {
        Map<String, URL> internalUrls = new HashMap<>();

        Pattern patternToFound = Pattern.compile(ANCHOR_LINK_REGEX, Pattern.DOTALL);

        for (String line : lines) {
            Matcher matcherResults = patternToFound.matcher(line);

            while (matcherResults.find()) {
                String urlString = matcherResults.group().split("\"")[1];

                if (!shouldIncludeUrl(urlString)) {
                    continue;
                }

                URI uri = URI.create(urlString);

                URL url = constructNewUrl(uri, actualUrl);

                if (url == null) {
                    continue;
                }

                urlString = url.toString();

                if (!this.isUrlAlreadyFound(urlString) && !this.isUrlAlreadyVisited(urlString) && !internalUrls.containsKey(urlString)) {
                    internalUrls.put(urlString, url);
                    this.foundUrls.add(urlString);
                }
            }
        }
        return internalUrls;
    }


    private Boolean isUrlAlreadyFound(String url) {
        return this.foundUrls.contains(url);
    }

    private Boolean isUrlAlreadyVisited(String url) {
        return this.visitedUrls.contains(url);
    }

    private Boolean isFinalThreadInteraction() {
        return totalUrlsToVisit.get() == visitedUrlsCount.get();
    }

    private URL constructNewUrl(URI uri, String currentUrl) throws MalformedURLException {
        String uriString = uri.toString();

        if (uri.isAbsolute() && uriString.contains(currentUrl)) {
            return new URL(uriString);
        }

        if (uriString.startsWith("../")) {
            return new URL(this.url + uriString.replace("../", ""));
        }

        if (!uri.isAbsolute()) {
            return new URL(this.url + uri);
        }

        return null;
    }

    private boolean shouldIncludeUrl(String urlString) {
        return urlString.endsWith(HTML_EXTENSION) && !urlString.startsWith(MAILTO_PREFIX);
    }

    private void updateSearchTermFoundUrls(SearchTerm searchTerm) {
        repository.update(searchTerm);
    }

    private void updateSearchTermStatusToDone(SearchTerm searchTerm) {
        searchTerm.setStatus(SearchStatus.done);
        repository.update(searchTerm);
    }

}
