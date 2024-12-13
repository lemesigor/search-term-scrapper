package com.searchterm.backend.unit.infrastructure;

import com.searchterm.backend.application.domain.SearchTerm;
import com.searchterm.backend.infrastructure.storage.SearchTermRepositoryInMemory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class StorageTest {

    private SearchTermRepositoryInMemory repository;

    @Mock
    private SearchTerm mockSearchTerm;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = SearchTermRepositoryInMemory.getInstance();
    }

    @Test
    void saveShouldAddSearchTermToRepositoryAndGetId() {
        String word = "TestWord";
        when(mockSearchTerm.getId()).thenReturn("TestId");
        when(mockSearchTerm.getWord()).thenReturn(word);

        String id = repository.save(word);

        assertThat(id, is(notNullValue()));
        assertThat(repository.findById(id), is(not(Optional.empty())));
    }

    @Test
    void findByIdShouldReturnOptionalEmptyForNonExistentId() {
        Optional<SearchTerm> result = repository.findById("NonExistentId");

        assertThat(result, is(Optional.empty()));
    }

    @Test
    void findByIdShouldReturnSearchTermForExistingId() {
        String id = repository.save("TestWord");

        Optional<SearchTerm> result = repository.findById(id);

        assertThat(result, is(not(Optional.empty())));
        assertThat(result.get().getId(), equalTo(id));
    }

    @Test
    void updateShouldUpdateExistingSearchTerm() {
        String id = repository.save("TestWord");
        when(mockSearchTerm.getId()).thenReturn(id);

        repository.update(mockSearchTerm);

        assertThat(repository.findById(id), is(not(Optional.empty())));
        assertThat(repository.findById(id).get().getId(), equalTo(id));
    }

}

