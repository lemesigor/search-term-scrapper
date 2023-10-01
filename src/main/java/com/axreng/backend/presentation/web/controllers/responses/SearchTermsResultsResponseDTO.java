package com.axreng.backend.presentation.web.controllers.responses;

import com.axreng.backend.application.domain.SearchStatus;
import com.axreng.backend.application.domain.SearchTerm;

import java.util.TreeSet;

public final class SearchTermsResultsResponseDTO {
    private final String id;
    private final SearchStatus status;
    private final TreeSet<String> urls;


    private SearchTermsResultsResponseDTO(String id,  TreeSet<String> urls, SearchStatus status) {
        this.id = id;
        this.urls = urls;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status.toString();
    }

    public TreeSet<String> getUrls() {
        return urls;
    }

    public static SearchTermsResultsResponseDTO fromDomain(SearchTerm searchTerm) {
        return new SearchTermsResultsResponseDTO(searchTerm.getId(), searchTerm.getUrls(), searchTerm.getStatus());
    }
}
