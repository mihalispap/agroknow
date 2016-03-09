package com.agroknow.linkchecker.exceptions;

public class LinkCheckingException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public LinkCheckingException() {
        super();
    }

    /**
     * @param message
     * @param cause
     */
    public LinkCheckingException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public LinkCheckingException(String message) {
        super(message);
    }
}
