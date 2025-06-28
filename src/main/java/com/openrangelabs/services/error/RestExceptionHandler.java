package com.openrangelabs.services.error;

import com.openrangelabs.services.user.model.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("A bad request was made to the server." + ex);

        do {
            String name = request.getParameterNames().next();
            log.error("ParameterName: "+name);
            log.error("Value: "+request.getParameterValues(name));
        } while(request.getParameterNames().hasNext());

        UserResponse errorUser = new UserResponse();
        errorUser.setError("Malformed JSON request");
        return new ResponseEntity<>(errorUser, HttpStatus.BAD_REQUEST);
    }

    //other exception handlers below
    @ExceptionHandler({ Exception.class })
    public ResponseEntity<UserResponse> handleInvalidParam(Exception ex, WebRequest request) {
        log.error(String.valueOf(ex));
        UserResponse errorUser = new UserResponse();
        errorUser.setError("Something went wrong please try again.");
        return new ResponseEntity<>(errorUser, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}