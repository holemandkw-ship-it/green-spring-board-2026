package com.green.spring_board.exception;

public class UserRequestException extends RuntimeException {
    public UserRequestException(String message) {
        super(message);
    }
}
