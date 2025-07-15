package com.util.command.exceptions;

public class ArgumentNotFoundException extends RuntimeException {
    public ArgumentNotFoundException(String message) {
        super(message);
    }
}
