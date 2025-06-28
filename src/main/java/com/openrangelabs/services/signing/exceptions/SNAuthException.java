package com.openrangelabs.services.signing.exceptions;

import com.openrangelabs.services.signing.dao.AuthError;

public class SNAuthException extends SNException {
    private AuthError.Type authError = AuthError.Type.UNKNOWN;

    public SNAuthException(AuthError.Type error) {
        this.authError = error;
    }

    public SNAuthException(String message) {
        super(message);
    }

    public SNAuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthError.Type getAuthError() {
        return authError;
    }

    public void setAuthError(AuthError.Type authError) {
        this.authError = authError;
    }
}
