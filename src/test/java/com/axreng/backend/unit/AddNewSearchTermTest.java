package com.axreng.backend.unit;

import com.axreng.backend.application.usecases.AddNewSearchTermUseCase;
import com.axreng.backend.infrastructure.storage.SearchTermRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;


public class AddNewSearchTermTest {
    @Mock
    private SearchTermRepository repository;

    private AddNewSearchTermUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new AddNewSearchTermUseCase(repository);
    }

    @Test
    void executeShouldSaveNewSearchTermAndReturnId() {
        String word = "TestWord";
        String expectedId = "TestId";

        when(repository.save(word)).thenReturn(expectedId);

        String resultId = useCase.execute(word);

        verify(repository, times(1)).save(word);
        assertThat(resultId, is(expectedId));
    }
}
