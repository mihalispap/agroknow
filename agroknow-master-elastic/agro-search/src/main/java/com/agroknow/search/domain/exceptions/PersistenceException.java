
package com.agroknow.search.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author aggelos
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class PersistenceException extends RuntimeException {

    public PersistenceException() {
    }

    public PersistenceException(String message) {
        super(message);
    }

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersistenceException(Throwable cause) {
        super(cause);
    }

}
