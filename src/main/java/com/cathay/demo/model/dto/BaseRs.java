package com.cathay.demo.model.dto;

import com.cathay.demo.model.enumeration.RequestStatus;
import lombok.Data;

/**
 * 統一回傳外殼型別，若是 Rs 忘了指定，Aop 會包
 * @param <T> 實際想回傳的物件
 */
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
