package com.axreng.backend.application.usecases;

import com.axreng.backend.application.domain.SearchStatus;
import com.axreng.backend.application.domain.SearchTerm;
import com.axreng.backend.infrastructure.cache.SiteMapCache;
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

    private final SiteMapCache siteMapCache;

    public ScrapeTermUseCase(SearchTermRepository repository, TaskQueue poolService, HtmlParser htmlParser, SiteMapCache siteMapCache) {
        this.repository = repository;
        this.visitedUrls = new HashSet<>();
        this.foundUrls = new HashSet<>();
        this.totalUrlsToVisit = new AtomicInteger(1);
        this.visitedUrlsCount = new AtomicInteger(0);
        this.poolService = poolService;
        this.htmlParser = htmlParser;
        this.siteMapCache = siteMapCache;
    }

    public void execute(String termId, String baseUrl) {
        try {
            this.temporarySearchTerm = repository.findById(termId)
                    .orElseThrow(() -> new NoSuchElementException("termId not found"));

            this.url = baseUrl;

            logger.info("Starting scraper for url: " + this.url + " and term: " + this.temporarySearchTerm.getWord());

            CompletableFuture.runAsync(() -> poolService.addTask(() -> scrape(this.url, this.temporarySearchTerm.getWord())), poolService.getExecutor());
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage());
        }
    }


    private void scrape(String currentUrl, String term) {
        List<String> htmlContentLinesList;
        try {
            if (!this.siteMapCache.containsKey(currentUrl)) {
                htmlContentLinesList = this.htmlParser.parseContentAsStringList(currentUrl);
                this.siteMapCache.put(currentUrl, htmlContentLinesList);
            } else {
                htmlContentLinesList = this.siteMapCache.get(currentUrl);
            }

            if (this.htmlParser.hasKeywordInHtmlAsStringList(htmlContentLinesList, term)) {
                this.temporarySearchTerm.addUrl(currentUrl);
                updateSearchTermFoundUrls(this.temporarySearchTerm);
            }

            HashSet<String> internalUrls = findInternalUrlsForCurrentPage(htmlContentLinesList, currentUrl);

            for (String internalUrl : internalUrls) {

                if (!this.isUrlAlreadyVisited(internalUrl)) {
                    totalUrlsToVisit.incrementAndGet();
                    poolService.addTask(() -> scrape(internalUrl, term));
                    foundUrls.add(internalUrl);
                }
            }

            visitedUrls.add(currentUrl);
            visitedUrlsCount.incrementAndGet();

            if (isFinalThreadInteraction()) {
                logger.info("finished scraper for url: " + this.url + " and id: " + this.temporarySearchTerm.getId());

                updateSearchTermStatusToDone(this.temporarySearchTerm);
            }

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private HashSet<String> findInternalUrlsForCurrentPage(List<String> htmlList, String actualUrl) throws MalformedURLException {
        HashSet<String> internalFoundedUrls = new HashSet<>();

        Pattern patternToFound = Pattern.compile(ANCHOR_LINK_REGEX, Pattern.DOTALL);

        for (String line : htmlList) {
            Matcher matcherResults = patternToFound.matcher(line);

            while (matcherResults.find()) {
                String candidateUrlString = matcherResults.group().split("\"")[1];

                if (!shouldIncludeUrl(candidateUrlString)) {
                    continue;
                }

                String url = createUrlFullPath(candidateUrlString, actualUrl);

                if (!url.isEmpty() && !isUrlAlreadyFoundOrVisited(url)) {
                    internalFoundedUrls.add(url);
                    this.foundUrls.add(url);
                }
            }
        }
        return internalFoundedUrls;
    }


    private Boolean isUrlAlreadyFoundOrVisited(String urlString) {
        return this.isUrlAlreadyFound(urlString) || this.isUrlAlreadyVisited(urlString);
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

    private String createUrlFullPath(String candidateUrl, String currentUrl) throws MalformedURLException {
        URI uri = URI.create(candidateUrl);

        if (uri.isAbsolute() && candidateUrl.contains(currentUrl)) {
            return new URL(candidateUrl).toString();
        }

        if (candidateUrl.startsWith("../")) {
            return new URL(this.url + candidateUrl.replace("../", "")).toString();
        }

        if (!uri.isAbsolute()) {
            return new URL(this.url + uri).toString();
        }

        return "";
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
