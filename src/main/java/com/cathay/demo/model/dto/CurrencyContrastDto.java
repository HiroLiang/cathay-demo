package com.cathay.demo.model.dto;

import com.cathay.demo.db.entity.CurrencyNameContrast;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 幣別對照 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyContrastDto {

    private String code;

    private String description;

    private String chineseName;

    /**
     * 轉換 entity，避免 entity 外露
     * @param entity entity
     */
    public CurrencyContrastDto(CurrencyNameContrast entity) {
        this.code = entity.getCode();
        this.description = entity.getDescription();
        this.chineseName = entity.getChineseName();
    }
}
