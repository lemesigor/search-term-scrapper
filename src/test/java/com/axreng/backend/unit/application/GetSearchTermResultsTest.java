package com.axreng.backend.unit.application;
import com.axreng.backend.application.domain.SearchTerm;
import com.axreng.backend.application.usecases.GetSearchTermResultsUseCase;
import com.axreng.backend.infrastructure.storage.SearchTermRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class GetSearchTermResultsTest {

    @Mock
    private SearchTermRepository repository;

    private GetSearchTermResultsUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new GetSearchTermResultsUseCase(repository);
    }

    @Test
    void executeShouldReturnOptionalOfSearchTerm() {
        String searchTermId = "TestId";
        SearchTerm expectedSearchTerm = new SearchTerm("TestWord");

        when(repository.findById(searchTermId)).thenReturn(Optional.of(expectedSearchTerm));

        Optional<SearchTerm> result = useCase.execute(searchTermId);

        verify(repository, times(1)).findById(searchTermId);
        assertThat(result, is(notNullValue()));
        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), is(sameInstance(expectedSearchTerm)));
    }

    @Test
    void executeShouldReturnEmptyOptionalIfNotFound() {
        String searchTermId = "NonExistentId";

        when(repository.findById(searchTermId)).thenReturn(Optional.empty());

        Optional<SearchTerm> result = useCase.execute(searchTermId);

        verify(repository, times(1)).findById(searchTermId);
        assertThat(result, is(notNullValue()));
        assertThat(result.isEmpty(), is(true));
    }
}

