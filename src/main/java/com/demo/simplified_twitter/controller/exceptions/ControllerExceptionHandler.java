package com.demo.simplified_twitter.controller.exceptions;

import com.demo.simplified_twitter.dto.HttpErrorResponseDto;
import com.demo.simplified_twitter.exceptions.BadCredentialsException;
import com.demo.simplified_twitter.exceptions.ResourceAlreadyExistsException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpErrorResponseDto> badCredentialsException(BadCredentialsException e, HttpServletRequest req) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        HttpErrorResponseDto httpExceptionHandlerResponse = new HttpErrorResponseDto(
                Instant.now(),
                httpStatus.value(),
                "Bad Credentials",
                e.getMessage(),
                req.getRequestURI()
        );

        return new ResponseEntity<>(httpExceptionHandlerResponse, httpStatus);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<HttpErrorResponseDto> resourceAlreadyExistsException(ResourceAlreadyExistsException e, HttpServletRequest req) {
        HttpStatus httpStatus = HttpStatus.CONFLICT;

        HttpErrorResponseDto httpExceptionHandlerResponse = new HttpErrorResponseDto(
                Instant.now(),
                httpStatus.value(),
                "Resource Already Exists",
                e.getMessage(),
                req.getRequestURI()
        );

        return new ResponseEntity<>(httpExceptionHandlerResponse, httpStatus);
    }

}
