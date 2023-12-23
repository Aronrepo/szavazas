package com.aron.voting.exception;

public class SzavazasNotFoundException extends RuntimeException {
    public SzavazasNotFoundException(String message) {
        super(message);
    }
}
