package com.cathay.demo.task.currency;

import com.cathay.demo.model.dto.CoinDeskDto;
import com.cathay.demo.model.enumeration.RequestStatus;
import com.cathay.demo.model.enumeration.TaskTag;
import com.cathay.demo.service.currency.CoinDeskService;
import com.cathay.demo.service.ServiceCollector;
import com.cathay.demo.task.StandardTask;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CoinDeskCaller extends StandardTask<CoinDeskDto> {

    private final CoinDeskService coinDeskService;

    private CoinDeskDto result;

    public CoinDeskCaller(ServiceCollector collector) {
        this.coinDeskService = collector.getCoinDeskService();
    }

    @Override
    protected void doAction() {
        try {
            // 嘗試打 API
            this.result = coinDeskService.callCoinDesk();

            // 成功的話儲存結果
            createStore(TaskTag.COIN_DESK_RESPONSE, result);

            // 可能為 Response
            storeResponse(RequestStatus.OK, result);
        } catch (Exception e) {
            log.warn("Call CoinDesk API failed.", e);
            forceFailed();
            storeResponse(RequestStatus.API_FAIL, null);
        }
    }

    @Override
    public CoinDeskDto getData() {
        return this.result;
    }
}
