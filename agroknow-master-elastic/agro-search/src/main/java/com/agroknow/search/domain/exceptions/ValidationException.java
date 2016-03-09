
package com.agroknow.search.domain.exceptions;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author aggelos
 */
@ResponseStatus(HttpStatus.METHOD_FAILURE)
public class ValidationException extends RuntimeException {

    Map<String,String> errorMessages = new HashMap<String,String>();

    public ValidationException() {
    }

    public ValidationException(String globalMessage, Map<String,String> errorMessages) {
        super(globalMessage);
        this.errorMessages = errorMessages;
    }

    public ValidationException(String globalMessage, Throwable cause, Map<String,String> errorMessages) {
        super(globalMessage, cause);
        this.errorMessages = errorMessages;
    }

    public ValidationException addErrorMessage(String field, String message) {
        this.errorMessages.put(field, message);
        return this;
    }

    public Map<String,String> getErrorMessages() {
        return this.errorMessages;
    }

}
