package com.openrangelabs.services.signing.exceptions;

import com.openrangelabs.services.signing.dao.Errors;

import java.util.List;
import java.util.stream.Collectors;

public class SNApiException extends SNException {
    List<Errors.ErrorInfo> errorInfo;

    public SNApiException(List<Errors.ErrorInfo> errors) {
        this.errorInfo = errors;
    }

    @Override
    public String getMessage() {
        String mess = super.getMessage();
        if (mess != null) {
            return mess;
        }
        if (errorInfo != null) {
            return errorInfo.stream().map(e -> e.code + ": " + e.message).collect(Collectors.joining("\\n"));
        }
        return null;
    }

    public SNApiException(String message) {
        super(message);
    }

    public SNApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
