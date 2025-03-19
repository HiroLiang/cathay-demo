package com.cathay.demo.model.enumeration;

import com.cathay.demo.db.entity.CurrencyNameContrast;
import com.cathay.demo.model.dto.CoinDeskDto;
import com.cathay.demo.model.dto.CoinDeskInfoDto;
import lombok.Getter;

import java.util.List;

/**
 * 任務 Storage 用的標記，用來定義.判斷存取該有的型別
 */
@Getter
public enum TaskTag {
    /**
     * BaseRs Builder 用
     */
    RS_STATUS(RequestStatus.class),
    RS_OBJECT(Object.class),

    /**
     * Currency DB 相關
     */
    CURRENCY_CONTRAST_DATA(CurrencyNameContrast.class),
    CURRENCY_CONTRAST_DATA_ALL(List.class),

    /**
     * Coin Desk 外部 API 相關
     */
    COIN_DESK_RESPONSE(CoinDeskDto.class),
    COIN_DESK_NEW_CURRENCY(CoinDeskInfoDto.CurrencyInfo.class),
    ;

    private final Class<?> clazz;

    TaskTag(Class<?> clazz) {
        this.clazz = clazz;
    }
}
