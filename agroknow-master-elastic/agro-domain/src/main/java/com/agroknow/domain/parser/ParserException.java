package com.agroknow.domain.parser;

public class ParserException extends java.lang.Exception {
    private static final long serialVersionUID = 1L;
    private String message;

    public ParserException(String message) {
        super();
        this.message = message;
    }

    /**
     * @param message
     * @param cause
     */
    public ParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getMessage() {
        return message;
    }

}
