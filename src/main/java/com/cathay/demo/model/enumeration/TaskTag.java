package com.cathay.demo.model.enumeration;

import com.cathay.demo.db.entity.CurrencyNameContrast;
import lombok.Getter;

import java.util.List;

@Getter
public enum TaskTag {
    /**
     * BaseRs Builder 用
     */
    RS_STATUS(RequestStatus.class),
    RS_OBJECT(Object.class),

    CURRENCY_CONTRAST_DATA(CurrencyNameContrast.class),
    CURRENCY_CONTRAST_DATA_ALL(List.class);

    ;

    private final Class<?> clazz;

    TaskTag(Class<?> clazz) {
        this.clazz = clazz;
    }
}
