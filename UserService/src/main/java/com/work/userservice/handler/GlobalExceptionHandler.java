package com.work.userservice.handler;

import com.work.userservice.exception.LoginFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<Map<String,Object>> handleLoginFailedException(LoginFailedException e){
        Map<String,Object> result = new HashMap<>();
        result.put("code",401);
        result.put("message",e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
    }

}
