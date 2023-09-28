package com.axreng.backend.application.domain;

import com.axreng.backend.shared.Errors;
import com.axreng.backend.shared.RandomIdGenerator;

public class SearchTerm {
    private final String word;
    private final String id;

    public SearchTerm(String word) {
        if (isValidWord(word)) {
            this.word = word;
            this.id = RandomIdGenerator.generateRandomId(8);
        } else {
            throw new IllegalArgumentException(Errors.INVALID_WORD_ERROR);
        }
    }

    public String getId() {
        return id;
    }

    public String getWord() {
        return word;
    }

    private boolean isValidWord(String word) {
        int maximumWordLength = 32;
        int minimumWordLength = 4;
        return word.length() <= maximumWordLength && word.length() >= minimumWordLength;
    }
}
