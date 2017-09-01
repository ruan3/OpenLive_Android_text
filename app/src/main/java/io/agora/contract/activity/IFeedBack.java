package io.agora.contract.activity;

import java.util.List;

import io.agora.model.FeedBackBean;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/8/30
 * Description:
 */

public interface IFeedBack {

    public void updataResult(int code,String result);
    public void getDataResult(int code, List<FeedBackBean> feedBackBeen);

}
