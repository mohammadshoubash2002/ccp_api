package com.mohammadshoubash.ccp_api.exception;

public class RecourceNotFoundException extends RuntimeException {
    public RecourceNotFoundException(String message) {
        super(message);
    }
}