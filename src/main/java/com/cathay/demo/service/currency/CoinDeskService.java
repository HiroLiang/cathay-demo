package com.cathay.demo.service.currency;

import com.cathay.demo.model.dto.CoinDeskDto;
import com.cathay.demo.util.ApiUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Call 外部 Api coin desk 取得 json
 */
@Slf4j
@Service
public class CoinDeskService {

    private final ApiUtil apiUtil;

    @Value("${coin-desk.api.url:https://kengp3.github.io/blog/coindesk.json}")
    private String COIN_DESK_URL;

    public CoinDeskService(ApiUtil apiUtil) {
        this.apiUtil = apiUtil;
    }

    public CoinDeskDto callCoinDesk() {
        return apiUtil.sendGet(COIN_DESK_URL, CoinDeskDto.class);
    }


}
