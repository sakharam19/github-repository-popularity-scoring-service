package com.popularity.scoring.exceptionhandling;

public class PageLimitExceededException extends RuntimeException {
    public PageLimitExceededException(String message) {
        super(message);
    }
}
