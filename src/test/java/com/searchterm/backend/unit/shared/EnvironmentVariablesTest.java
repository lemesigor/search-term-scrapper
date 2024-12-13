package com.searchterm.backend.unit.shared;

import com.searchterm.backend.shared.EnvironmentVariables;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.MalformedURLException;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class EnvironmentVariablesTest {
    private static final String VALID_URL = "http://avalidurl.com/";
    private static final String INVALID_URL = "invalid_url";
    private static final String ENV_VARIABLE_NAME = "BASE_URL";

    private static final EnvironmentVariables environmentVariables = EnvironmentVariables.getInstance();

    @Mock
    private Function<String, String> envGetter;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetBaseURLWithValidURL() throws MalformedURLException {
        when(envGetter.apply(ENV_VARIABLE_NAME)).thenReturn(VALID_URL);
        String result = environmentVariables.getBaseURL(envGetter);
        assertEquals(VALID_URL , result);
    }

    @Test
    public void testGetBaseURLWithNullURL() throws MalformedURLException {
        when(envGetter.apply(ENV_VARIABLE_NAME)).thenReturn(null);
        String result = environmentVariables.getBaseURL(envGetter);
        assertEquals(EnvironmentVariables.DEFAULT_URL, result);
    }

    @Test
    public void testGetBaseURLWithInvalidURL() {
        when(envGetter.apply(ENV_VARIABLE_NAME)).thenReturn(INVALID_URL);
        assertThrows(MalformedURLException.class, () -> environmentVariables.getBaseURL(envGetter));
    }

}
