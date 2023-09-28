package com.axreng.backend.application.domain;

import com.axreng.backend.shared.RandomIdGenerator;

public class Url {
    /**
     * The url that will be received from the request
     * It should have the actual validated url and generate the id
     * with a random 8 character string
     * <p>
     * It should also have methods to see if a url is valid and if it's relative url from the main url
     */

    private final String url;
    private final String id;

    public Url(String url) {
        this.url = url;
        this.id = RandomIdGenerator.generateRandomId(8);
    }


    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }


}