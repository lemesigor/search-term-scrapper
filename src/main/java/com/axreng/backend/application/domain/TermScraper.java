package com.axreng.backend.application.domain;

import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.log.Slf4jLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TermScraper {

    private String url;
    private String term;

    private Set<String> visitedUrls;
    private Set<String> foundUrls;

    private Set<String> resultSet;
    private Logger logger = new Slf4jLog("TermScraper");

    private final ExecutorService executor;


    private final AtomicInteger count;

    public TermScraper(String url, String term) {
        this.url = url;
        this.term = term;
        this.visitedUrls = new HashSet<>();
        this.foundUrls = new HashSet<>();
        this.resultSet = new HashSet<>();
        this.executor = Executors.newFixedThreadPool(4);
        this.count = new AtomicInteger(0);
    }

    public String getUrl() {
        return url;
    }

    public String getTerm() {
        return term;
    }

    public void execute() {

        try {
            executor.submit(() -> scrape(this.url));

            // Wait a while for existing tasks to terminate
            if (!executor.awaitTermination(480, TimeUnit.SECONDS) && count.get() > 0) {
                executor.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!executor.awaitTermination(100, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ex) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }


    private void scrape(String currentUrl) {
        count.incrementAndGet();
        try {
            final URL urlObj = new URL(currentUrl);

            List<String> lines = this.getHtmlLines(urlObj);


            if (findKeyword(lines, this.term)) {
                logger.info("achou");
                this.resultSet.add(currentUrl);

                logger.info("Result set atualizado: " + this.resultSet);
            }


            Map<String, URL> internalUrls = findInternalUrls(lines, currentUrl);



            for (URL internalUrl : internalUrls.values()) {
                String newUrl = internalUrl.toString();

                if (!visitedUrls.contains(newUrl)) {
                    executor.submit(() -> scrape(newUrl));
                    foundUrls.add(newUrl);
                }
            }
            visitedUrls.add(currentUrl);

        } catch (IOException e) {
            logger.warn(e);
        }

        count.decrementAndGet();

        if (count.get() == 0) {
            logger.info("result set final: " + this.resultSet);
            this.executor.shutdown();
        }
    }

    private Map<String, URL> findInternalUrls(List<String> lines, String actualUrl) throws MalformedURLException {
        Map<String, URL> internalUrls = new HashMap<>();

        Pattern patternToFound = Pattern.compile("href=\"(.*?)\"", Pattern.DOTALL);

        for (String line : lines) {
            Matcher matcherResults = patternToFound.matcher(line);

            while (matcherResults.find()) {
                String urlString = matcherResults.group().split("\"")[1];

                if (!urlString.endsWith(".html") || urlString.startsWith("mailto")) {
                    continue;
                }
                URI uri = URI.create(urlString);

                URL url = contructNewUrl(uri, actualUrl);

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

    private URL contructNewUrl(URI uri, String currentUrl) throws MalformedURLException {
        String uriString = uri.toString();

        if (uri.isAbsolute()) {
            if (uriString.contains(currentUrl)) {
                return new URL(uri.toString());
            }
        } else if (uriString.startsWith("../")) {
            return new URL(this.url + uri.toString().replace("../", ""));
        } else {
            return new URL(this.url + uri);
        }

        return null;
    }

    // achar keyword e url em um loop só
    private Boolean findKeyword(List<String> lines, String keyword) {
        String keywordPattern = "\\b" + keyword + "\\b";
        Pattern p = Pattern.compile(keywordPattern, Pattern.CASE_INSENSITIVE);
        return lines.stream().anyMatch(line -> p.matcher(line).find());
    }


    public List<String> getHtmlLines(URL url) throws IOException {

        BufferedReader htmlLines = new BufferedReader(new InputStreamReader(url.openStream()));
        Stream<String> lines = htmlLines.lines();
        htmlLines.close();

        return lines.collect(Collectors.toList());
    }
}
