package com.work.userservice.exception;

public class LoginFailedException extends RuntimeException{
    public LoginFailedException(String message)
    {
        super(message);
    }
}
