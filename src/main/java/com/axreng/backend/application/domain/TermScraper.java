package com.axreng.backend.application.domain;

import com.axreng.backend.shared.HtmlNormalizationHelper;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.log.Slf4jLog;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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
    private Logger logger = new Slf4jLog("TermScraper")


    public TermScraper(String url, String term) {
        this.url = url;
        this.term = term;
        this.visitedUrls = new HashSet<>();
        this.foundUrls = new HashSet<>();
        this.resultSet = new HashSet<>();
    }

    public String getUrl() {
        return url;
    }

    public String getTerm() {
        return term;
    }

    public void execute() {
        ExecutorService executor = Executors.newFixedThreadPool(4);

        executor.submit(() -> scrape(this.url));

        executor.shutdown();

        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            // Handle the exception
            e.printStackTrace();
        }
    }

    private void scrape(String currentUrl) {

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
                scrape(internalUrl.toString());
            }
            visitedUrls.add(currentUrl);


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, URL> findInternalUrls(List<String> lines, String actualUrl) throws MalformedURLException {
        Map<String, URL> internalUrls = new HashMap<>();

        Pattern p = Pattern.compile("href=\"(.*?)\"", Pattern.DOTALL);

        for (String line : lines) {
            Matcher m = p.matcher(line);

            while (m.find()) {
                String urlStr = m.group().split("\"")[1];


                if (!urlStr.endsWith(".html") || urlStr.startsWith("mailto")) {
                    continue;
                }
                URI uri = URI.create(urlStr);

                URL url = contructUrl(uri, actualUrl);

                if (url == null) {
                    continue;
                }

                urlStr = url.toString();
                if (!this.foundUrls.contains(urlStr) && !this.visitedUrls.contains(urlStr) && !internalUrls.containsKey(urlStr)) {
                    internalUrls.put(urlStr, url);
                    this.foundUrls.add(urlStr);
                }
            }
        }
        return internalUrls;
    }


    private URL contructUrl(URI uri, String currentUrl) throws MalformedURLException {
        String uriStr = uri.toString();

        if (uri.isAbsolute()) {
            if (uriStr.contains(currentUrl)) {
                return new URL(uri.toString());
            }
        } else if (uriStr.startsWith("../")) {
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

        return lines.collect(Collectors.toList());
    }
}
