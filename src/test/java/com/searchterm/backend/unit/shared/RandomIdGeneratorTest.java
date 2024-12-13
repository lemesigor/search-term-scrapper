package com.searchterm.backend.unit.shared;

import com.searchterm.backend.shared.RandomIdGenerator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RandomIdGeneratorTest {

    @Test
    public void testGenerateRandomId() {
        String randomId1 = RandomIdGenerator.generateRandomId(10);
        String randomId2 = RandomIdGenerator.generateRandomId(20);

        assertEquals(10, randomId1.length());
        assertEquals(20, randomId2.length());

        for (char c : randomId1.toCharArray()) {
            assertTrue(isValidCharacter(c));
        }
        for (char c : randomId2.toCharArray()) {
            assertTrue(isValidCharacter(c));
        }
    }

    private boolean isValidCharacter(char c) {
        String validCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        return validCharacters.indexOf(c) != -1;
    }
}
