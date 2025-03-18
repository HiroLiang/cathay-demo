package com.cathay.demo.model.dto;

import com.cathay.demo.model.enumeration.RequestStatus;
import lombok.Data;

@Data
public class BaseRs<T> {

    private RequestStatus status;
    private String code;
    private String message;
    private T contain;

    public BaseRs(RequestStatus status, String message, T contain) {
        this.status = status;
        this.code = status.getCode();
        this.message = message;
        this.contain = contain;
    }

    public BaseRs(T contain) {
        this.status = RequestStatus.OK;
        this.code = RequestStatus.OK.getCode();
        this.message = RequestStatus.OK.name();
        this.contain = contain;
    }


}
