package com.util.command.exceptions;

public class CommandNotFoundException extends RuntimeException {
    public CommandNotFoundException(String message) {
        super(message);
    }
}
