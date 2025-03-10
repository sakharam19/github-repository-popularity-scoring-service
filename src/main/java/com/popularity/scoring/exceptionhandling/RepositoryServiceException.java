package com.popularity.scoring.exceptionhandling;

public class RepositoryServiceException extends RuntimeException {
    public RepositoryServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepositoryServiceException(String message) {
        super(message);
    }
}