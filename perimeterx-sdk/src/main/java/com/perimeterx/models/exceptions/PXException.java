package com.perimeterx.models.exceptions;

/**
 * PerimeterX Exception
 * <p>
 * Created by shikloshi on 05/07/2016.
 */
public class PXException extends Exception {

    public PXException(Throwable cause) {
        super(cause);
    }

    public PXException(String message) {
        super(message);
    }

    public PXException(String message, Throwable cause) {
        super(message, cause);
    }
}
