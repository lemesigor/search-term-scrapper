package com.axreng.backend.application.domain;

import com.axreng.backend.shared.HtmlNormalizationHelper;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TermScraper {

    private String url;
    private String term;

    private Set<String> visitedUrls;
    private Set<String> foundUrls;

    private Set<String> resultSet;



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
        scrape(this.url);
    }
    private void scrape(String currentUrl) {
        final String httpsUrl = url;

        Boolean foundTermInCurrentUrl = false;
        try {
            final URL urlObj = new URL(httpsUrl);

            List<String> lines = this.getHtmlLines(urlObj);


            if(!foundTermInCurrentUrl && findKeyword(lines, this.term)) {
                System.out.println("achou");
                this.resultSet.add(currentUrl.toString());
            }

            System.out.println("****** Content of the URL ********");



        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private Boolean findKeyword(List<String> lines, String keyword) {
        System.out.println(lines);
        String keywordPattern = "\\b" + keyword + "\\b";
        Pattern p = Pattern.compile(keywordPattern, Pattern.CASE_INSENSITIVE);
        Boolean result = lines.stream().anyMatch(line -> p.matcher(line).find());
        System.out.println(result);
        return result;
    }


    public List<String> getHtmlLines(URL url) throws IOException {

        BufferedReader htmlLines = new BufferedReader(new InputStreamReader(url.openStream()));
        Stream<String> lines = htmlLines.lines();

        return lines.map(HtmlNormalizationHelper::normalizeHtml).collect(Collectors.toList());
    }
}
