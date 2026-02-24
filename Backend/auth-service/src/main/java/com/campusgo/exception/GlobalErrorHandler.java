package com.campusgo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.campusgo.exception.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.util.LinkedHashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handle(Exception e) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("error", e.getClass().getSimpleName());
        error.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex) {
        ErrorResponse resp = new ErrorResponse(
                "UNAUTHORIZED",
                ex.getMessage()
        );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);


    }
}
