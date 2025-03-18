package com.cathay.demo.db.entity;

import com.cathay.demo.model.dto.CurrencyContrastDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CURRENCY_NAME_CONTRAST")
public class CurrencyNameContrast {

    @Id
    @Column(name = "CODE", nullable = false, length = 10)
    private String code;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @Column(name = "CHINESE_NAME", nullable = false, length = 31)
    private String chineseName;

    @Column(name = "CREATE_TIME")
    private Date createTime;

    @Column(name = "MODIFY_TIME")
    private Date modifyTime;

    public CurrencyNameContrast(CurrencyContrastDto dto) {
        Date date = new Date();
        this.code = dto.getCode();
        this.description = dto.getDescription();
        this.chineseName = dto.getChineseName();
        this.createTime = date;
        this.modifyTime = date;
    }
}
