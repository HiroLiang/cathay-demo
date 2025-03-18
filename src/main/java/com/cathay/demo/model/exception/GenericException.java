package com.cathay.demo.model.exception;

import com.cathay.demo.model.enumeration.RequestStatus;
import lombok.Getter;

@Getter
public class GenericException extends RuntimeException {

    private final String code;
    private final String message;

    public GenericException(RequestStatus status) {
        super(status.name());
        this.code = status.getCode();
        this.message = status.name();
    }

    public GenericException(RequestStatus status, Throwable cause) {
        super(status.name(), cause);
        this.code = status.getCode();
        this.message = status.name();
    }

    public GenericException(RequestStatus status, String message) {
        super(status.name());
        this.code = status.getCode();
        this.message = message;
    }

    public GenericException(RequestStatus status, String message, Throwable cause) {
        super(status.name(), cause);
        this.code = status.getCode();
        this.message = message;
    }

}
