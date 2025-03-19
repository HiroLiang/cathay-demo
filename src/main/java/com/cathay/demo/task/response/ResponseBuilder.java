package com.cathay.demo.task.response;

import com.cathay.demo.model.dto.BaseRs;
import com.cathay.demo.model.enumeration.RequestStatus;
import com.cathay.demo.model.enumeration.TaskTag;
import com.cathay.demo.task.StandardTask;

public class ResponseBuilder<T> extends StandardTask<BaseRs<T>> {

    private BaseRs<T> response;

    @Override
    @SuppressWarnings("unchecked")
    protected void doAction() {
        RequestStatus status = receive(TaskTag.RS_STATUS, RequestStatus.class);
        T contain = (T) receive(TaskTag.RS_OBJECT, Object.class);

        this.response = new BaseRs<>(status, status.name(), contain);
    }

    @Override
    public BaseRs<T> getData() {
        return response;
    }

}
