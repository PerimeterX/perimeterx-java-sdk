package com.perimeterx.models.exceptions;

/**
 * Created by nitzangoldfeder on 13/04/2017.
 */
public class PXCookieDecryptionException extends Exception {

    public PXCookieDecryptionException(Throwable cause) {
        super(cause);
    }

    public PXCookieDecryptionException(String message) {
        super(message);
    }

    public PXCookieDecryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}