package com.agroknow.linkchecker.exceptions;

import java.net.MalformedURLException;

public class NotSupportedProtocolException extends MalformedURLException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public NotSupportedProtocolException() {
        super();
    }

    /**
     * @param message
     */
    public NotSupportedProtocolException(String message) {
        super(message);
    }
}
