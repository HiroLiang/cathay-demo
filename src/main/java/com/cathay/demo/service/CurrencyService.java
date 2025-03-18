package com.cathay.demo.service;

import com.cathay.demo.db.entity.CurrencyNameContrast;
import com.cathay.demo.db.repo.CurrencyNameContrastRepository;
import com.cathay.demo.model.dto.CurrencyContrastDto;
import com.cathay.demo.model.enumeration.RequestStatus;
import com.cathay.demo.model.exception.GenericException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * CurrencyNameContrast 基礎 DB 交互
 */
@Service
public class CurrencyService {

    public final CurrencyNameContrastRepository currencyNameContrastRepository;

    public CurrencyService(CurrencyNameContrastRepository currencyNameContrastRepository) {
        this.currencyNameContrastRepository = currencyNameContrastRepository;
    }

    @Transactional
    public List<CurrencyNameContrast> getAllContrasts() {
        return currencyNameContrastRepository.findAll();
    }

    @Transactional
    public CurrencyNameContrast getContrastByCode(String code) {
        return currencyNameContrastRepository.findById(code)
                .orElseThrow(() -> new GenericException(RequestStatus.DB_FAIL, "Data not found."));
    }

    @Transactional
    public CurrencyNameContrast saveContrast(CurrencyNameContrast currencyNameContrast) {
        return currencyNameContrastRepository.save(currencyNameContrast);
    }

    @Transactional
    public void deleteContrast(String code) {
        currencyNameContrastRepository.deleteById(code);
    }

    @Transactional
    public CurrencyNameContrast addContrast(CurrencyContrastDto dto) {
        try {
            getContrastByCode(dto.getCode());
            throw new GenericException(RequestStatus.DB_FAIL, "Data already exists.");
        } catch (GenericException e) {
            return saveContrast(new CurrencyNameContrast(dto));
        }
    }

    @Transactional
    public CurrencyNameContrast updateContrast(CurrencyContrastDto dto) {
        final CurrencyNameContrast contrast = getContrastByCode(dto.getCode());
        contrast.setDescription(dto.getDescription());
        contrast.setChineseName(dto.getChineseName());
        contrast.setModifyTime(new Date());
        return currencyNameContrastRepository.save(contrast);
    }
}
