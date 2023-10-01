package com.axreng.backend.application.usecases;

import com.axreng.backend.application.domain.SearchStatus;
import com.axreng.backend.application.domain.SearchTerm;
import com.axreng.backend.infrastructure.storage.SearchTermRepository;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.log.Slf4jLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ScrapeTermUseCase {


    private final String url;

    private final Set<String> visitedUrls;
    private final Set<String> foundUrls;

    private final Logger logger = new Slf4jLog("TermScraper");

    private final ExecutorService executor;


    private final SearchTermRepository repository;
    private final AtomicInteger count;

    private static final String HTML_EXTENSION = ".html";
    private static final String MAILTO_PREFIX = "mailto";

    private static final String ANCHOR_LINK_REGEX = "href=\"(.*?)\"";


    private static final Integer THREAD_TIMEOUT = 1200;

    private SearchTerm temporarySearchTerm;


    public ScrapeTermUseCase(SearchTermRepository repository, ExecutorService executor) {
        this.repository = repository;
        this.url = System.getenv("BASE_URL");
        this.visitedUrls = new HashSet<>();
        this.foundUrls = new HashSet<>();
        this.count = new AtomicInteger(0);
        this.executor = executor;
    }

    public void execute(String termId) {
        this.temporarySearchTerm = repository.findById(termId).get();

        logger.info("Starting scraper for url: " + this.url + " and term: " + this.temporarySearchTerm.getWord());

        CompletableFuture.runAsync(() -> {
            executor.submit(() -> scrape(this.url, this.temporarySearchTerm.getWord()));

        },executor);
    }


    private void scrape(String currentUrl, String term) {

        count.incrementAndGet();

        try {
            final URL urlObj = new URL(currentUrl);

            List<String> htmlContentLinesList = this.getHtmlContentLinesList(urlObj);

            if (searchKeyword(htmlContentLinesList, term)) {
                this.temporarySearchTerm.addUrl(currentUrl);
                updateSearchTermFoundUrls(this.temporarySearchTerm);
            }

            Map<String, URL> internalUrls = findInternalUrlsForCurrentPage(htmlContentLinesList, currentUrl);

            for (URL internalUrl : internalUrls.values()) {
                String newUrl = internalUrl.toString();

                if (!this.isUrlAlreadyVisited(newUrl)) {
                    executor.submit(() -> scrape(newUrl, term));
                    foundUrls.add(newUrl);
                }
            }

            visitedUrls.add(currentUrl);

        } catch (IOException e) {
            logger.warn(e);
        }

        count.decrementAndGet();

        if (isFinalThreadInteraction()) {
            logger.info("result set final de " + term + " "+ this.temporarySearchTerm.getUrls());

            updateSearchTermStatusToDone(this.temporarySearchTerm);

//            this.executor.shutdown();
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
        return count.get() == 0;
    }

    private URL constructNewUrl(URI uri, String currentUrl) throws MalformedURLException {
        String uriString = uri.toString();

        if (uri.isAbsolute() && uriString.contains(currentUrl)) {
            return new URL(uriString);
        }

        if (uriString.startsWith("../")){
            return new URL(this.url + uriString.replace("../", ""));
        }

        if (!uri.isAbsolute()) {
            return new URL(this.url + uri);
        }

        return null;
    }


    private Boolean searchKeyword(List<String> htmlAsLines, String keyword) {
        String keywordPattern = "\\b" + keyword + "\\b";
        Pattern patternToFound = Pattern.compile(keywordPattern, Pattern.CASE_INSENSITIVE);
        return htmlAsLines.stream().anyMatch(line -> patternToFound.matcher(line).find());
    }

    private boolean shouldIncludeUrl(String urlString) {
        return urlString.endsWith(HTML_EXTENSION) && !urlString.startsWith(MAILTO_PREFIX);
    }

    public List<String> getHtmlContentLinesList(URL url) throws IOException {

        BufferedReader htmlLines = new BufferedReader(new InputStreamReader(url.openStream()));
        Stream<String> lines = htmlLines.lines();

        var linesList = lines.collect(Collectors.toList());

        htmlLines.close();

        return linesList;
    }

    private void updateSearchTermFoundUrls(SearchTerm searchTerm) {
        repository.update(searchTerm);
    }

    private void updateSearchTermStatusToDone(SearchTerm searchTerm) {
        searchTerm.setStatus(SearchStatus.done);
        repository.update(searchTerm);
    }

}
