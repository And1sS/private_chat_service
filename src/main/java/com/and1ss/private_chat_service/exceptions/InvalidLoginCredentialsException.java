package com.and1ss.private_chat_service.exceptions;

public class InvalidLoginCredentialsException extends RuntimeException {
    @Override
    public String getMessage() {
        return "Incorrect login or password";
    }
}
