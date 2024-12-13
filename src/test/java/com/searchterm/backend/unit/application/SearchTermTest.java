package com.searchterm.backend.unit.application;

import com.searchterm.backend.application.domain.SearchStatus;
import com.searchterm.backend.application.domain.SearchTerm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SearchTermTest {

    private SearchTerm searchTerm;

    private final String validWord = "ValidWord";

    private final String smallInvalidWord = "Sh";

    private final String bigInvalidWord = "ThisIsAReallyLongWordThatExceedsTheMaximumLengthOf32Characters";

    @BeforeEach
    void setUp() {
        searchTerm = null;
    }

    @Test
    void validWordShouldCreateSearchTermObject() {

        searchTerm = new SearchTerm(validWord);

        assertNotNull(searchTerm);
        assertEquals(validWord, searchTerm.getWord());
        assertNotNull(searchTerm.getId());
    }

    @Test
    void newWordShouldHaveActiveStatus() {

        searchTerm = new SearchTerm(validWord);

        assertEquals(SearchStatus.active, searchTerm.getStatus());
    }

    @Test
    void newWordShouldHaveEmptyUrls() {

        searchTerm = new SearchTerm(validWord);

        assertTrue(searchTerm.getUrls().isEmpty());
    }

    @Test
    void invalidSmallWordShouldThrowIllegalArgumentException() {

        assertThrows(IllegalArgumentException.class, () -> new SearchTerm(smallInvalidWord));
    }

    @Test
    void invalidBigWordShouldThrowIllegalArgumentException() {

        assertThrows(IllegalArgumentException.class, () -> new SearchTerm(bigInvalidWord));
    }

    @Test
    void wordLengthGreaterThan32ShouldThrowIllegalArgumentException() {
        String longWord = "ThisIsAReallyLongWordThatExceedsTheMaximumLengthOf32Characters";

        assertThrows(IllegalArgumentException.class, () -> new SearchTerm(longWord));
    }

    @Test
    void wordLengthEqualTo32ShouldCreateSearchTermObject() {
        String validWord = "Exactly32CharactersLong";

        searchTerm = new SearchTerm(validWord);

        assertNotNull(searchTerm);
        assertEquals(validWord, searchTerm.getWord());
    }

    @Test
    void addUrlShouldAddUrlToUrls() {
        searchTerm = new SearchTerm(validWord);
        String url = "https://someurl.com";

        searchTerm.addUrl(url);

        assertTrue(searchTerm.getUrls().contains(url));
    }

    @Test
    void setStatusShouldSetStatus() {
        searchTerm = new SearchTerm(validWord);
        SearchStatus newStatus = SearchStatus.done;

        searchTerm.setStatus(newStatus);

        assertEquals(newStatus, searchTerm.getStatus());
    }
}
