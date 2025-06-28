package com.mercado_pago.checkout.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ResponseStatus(HttpStatus.NOT_FOUND)
@Getter
public class GenericBadRequestException extends RuntimeException {

    private final List<String> args;

    public GenericBadRequestException(String message) {
        super(message);
        this.args = null;
    }

    public GenericBadRequestException(String message, List<String> args) {
        super(message);
        this.args = args;
    }

    public GenericBadRequestException(Throwable cause) {
        super(cause.getMessage() != null ? cause.getMessage() : "Bad Request", cause);
        this.args = null;
    }

}
