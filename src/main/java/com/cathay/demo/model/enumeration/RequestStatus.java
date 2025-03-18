package com.cathay.demo.model.enumeration;

import lombok.Getter;

@Getter
public enum RequestStatus {
    OK("0000"),
    API_FAIL("0001"),
    DB_FAIL("0002"),
    SYSTEM_ERROR("9999"),
    ;

    private final String code;

    RequestStatus(String code) {
        this.code = code;
    }

    public static RequestStatus getByCode(String code) {
        for (RequestStatus status : RequestStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return SYSTEM_ERROR;
    }
}
